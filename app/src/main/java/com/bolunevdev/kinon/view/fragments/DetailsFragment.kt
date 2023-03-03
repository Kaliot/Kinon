package com.bolunevdev.kinon.view.fragments

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.transition.Fade
import androidx.transition.TransitionInflater
import com.bolunevdev.kinon.R
import com.bolunevdev.kinon.data.ApiConstants
import com.bolunevdev.kinon.data.entity.Film
import com.bolunevdev.kinon.databinding.FragmentDetailsBinding
import com.bolunevdev.kinon.view.activities.MainActivity
import com.bolunevdev.kinon.viewmodel.DetailsFragmentViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*


class DetailsFragment : Fragment() {
    private val viewModel: DetailsFragmentViewModel by activityViewModels()
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var film: Film
    private var favoriteFilms = mutableListOf<Film>()
    private lateinit var scopeIO: CoroutineScope


    init {
        enterTransition = Fade(Fade.IN).apply { duration = MainActivity.TRANSITION_DURATION }
        returnTransition = Fade(Fade.OUT).apply { duration = MainActivity.TRANSITION_DURATION }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity)
            .findViewById<FrameLayout>(R.id.background_share_transition)
            .visibility = View.VISIBLE

        loadFilmsDataBase()

        initSharedElementEnterTransition()

        setDetails()

        initDetailsFabDownloadWp()

        initDetailsFabShare()
    }

    private fun loadFilmsDataBase() {
        scopeIO = CoroutineScope(Dispatchers.IO)
        scopeIO.launch {
            viewModel.filmsListFlow.collect {
                withContext(Dispatchers.Main) {
                    favoriteFilms = it as MutableList<Film>
                    initDetailsFabFavorites()
                    setFabFavoritesIcon()
                }
            }
        }
    }

    private fun initDetailsFabFavorites() {
        binding.detailsFabFavorites.setOnClickListener {
            scopeIO.launch {
                if (!favoriteFilms.contains(film)) {
                    viewModel.addToFavoritesFilms(film)
                } else {
                    viewModel.deleteFromFavoritesFilms(film.id)
                }
            }
        }
    }

    private fun initDetailsFabDownloadWp() {
        binding.detailsFabDownloadWp.setOnClickListener {
            performAsyncLoadOfPoster()
        }
    }

    private fun initSharedElementEnterTransition() {
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.image_shared_element_transition)
    }

    private fun initDetailsFabShare() {
        binding.detailsFabShare.setOnClickListener {
            //Создаем интент
            val intent = Intent()
            //Указываем action с которым он запускается
            intent.action = Intent.ACTION_SEND
            //Кладем данные о нашем фильме
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "${getString(R.string.CheckOutThisFilm)} ${film.title} \n\n ${film.description}"
            )
            //Указываем MIME тип, чтобы система знала, какое приложения предложить
            intent.type = MIME_TYPE
            //Запускаем наше активити
            startActivity(Intent.createChooser(intent, getString(R.string.ShareTo)))
        }
    }

    private fun setDetails() {
        film = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //Получаем наш фильм из переданного бандла
            arguments?.getParcelable(
                MainActivity.KEY_FILM_DETAILS_FRAGMENT,
                Film::class.java
            ) as Film
        } else {
            //Получаем наш фильм из переданного бандла
            @Suppress("DEPRECATION")
            arguments?.getParcelable<Film>(MainActivity.KEY_FILM_DETAILS_FRAGMENT) as Film
        }

        //Устанавливаем заголовок
        binding.detailsPoster.transitionName = film.id.toString()
        binding.detailsToolbar.title = film.title

        //Устанавливаем картинку
        Glide.with(this)
            .load(ApiConstants.IMAGES_URL + IMAGE_SIZE + film.poster)
            .error(R.drawable.no_poster_holder)
            .centerCrop()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }
            })
            .into(binding.detailsPoster)

        //Устанавливаем описание
        binding.detailsDescription.text = film.description
    }

    private fun setFabFavoritesIcon() {
        binding.detailsFabFavorites.setImageResource(
            if (favoriteFilms.contains(film)) R.drawable.ic_baseline_favorite
            else R.drawable.ic_baseline_favorite_border_24
        )
    }

    private fun saveToGallery(bitmap: Bitmap) {
        //Проверяем версию системы
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //Создаем объект для передачи данных
            val contentValues = ContentValues().apply {
                //Составляем информацию для файла (имя, тип, дата создания, куда сохранять и т.д.)
                put(MediaStore.Images.Media.TITLE, film.title.handleSingleQuote())
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    film.title.handleSingleQuote()
                )
                put(MediaStore.Images.Media.MIME_TYPE, IMAGE_TYPE)
                put(
                    MediaStore.Images.Media.DATE_ADDED,
                    System.currentTimeMillis() / MILLIS_TO_SEC_RATIO
                )
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.RELATIVE_PATH, DOWNLOAD_PATH)
            }
            //Получаем ссылку на объект Content resolver, который помогает передавать информацию из приложения вовне
            val contentResolver = requireActivity().contentResolver
            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            //Открываем канал для записи на диск
            val outputStream = contentResolver.openOutputStream(uri!!)
            //Передаем нашу картинку, может сделать компрессию
            bitmap.compress(Bitmap.CompressFormat.JPEG, PERCENT_OF_QUALITY, outputStream)
            //Закрываем поток
            outputStream?.close()
        } else {
            //То же, но для более старых версий ОС
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver,
                bitmap,
                film.title.handleSingleQuote(),
                film.description.handleSingleQuote()
            )
        }
    }

    private fun String.handleSingleQuote(): String {
        return this.replace("'", "")
    }

    private fun performAsyncLoadOfPoster() {
        //Проверяем есть ли разрешение
        if (!checkPermission()) {
            //Если нет, то запрашиваем и выходим из метода
            requestPermission()
            return
        }
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            Toast.makeText(
                requireContext(),
                TOAST_ERROR_MESSAGE,
                Toast.LENGTH_SHORT
            ).show()
        }
        //Создаем родительский скоуп с диспатчером Main потока, так как будем взаимодействовать с UI
        val mainScope = CoroutineScope(Dispatchers.Main)
        mainScope.launch(exceptionHandler) { //Включаем Прогресс-бар
            binding.progressBar.isVisible = true
            //Создаем через async, так как нам нужен результат от работы, то есть Bitmap
            val job = scopeIO.async {
                viewModel.loadWallpaper(ApiConstants.IMAGES_URL + "original" + film.poster)
            }
            supervisorScope {
                launch(exceptionHandler) {
                    //Сохраняем в галерею, как только файл загрузится
                    saveToGallery(job.await())
                    //Выводим снекбар с кнопкой перейти в галерею
                    Snackbar.make(
                        binding.root,
                        R.string.downloaded_to_gallery,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.open) {
                            val intent = Intent()
                            intent.action = Intent.ACTION_VIEW
                            intent.type = "image/*"
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .show()
                }
            }
            //Отключаем Прогресс-бар
            binding.progressBar.isVisible = false
        }
        println(mainScope.isActive)
    }

    //Узнаем, было ли получено разрешение ранее
    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    //Запрашиваем разрешение
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            WRITE_EXTERNAL_STORAGE_PERMISSION_CODE
        )
    }

    override fun onStop() {
        super.onStop()
        scopeIO.cancel()
    }

    companion object {
        private const val MIME_TYPE = "text/plain"
        private const val IMAGE_SIZE = "w780"
        private const val MILLIS_TO_SEC_RATIO = 1000
        private const val IMAGE_TYPE = "image/jpeg"
        private const val DOWNLOAD_PATH = "Pictures/Kinon"
        private const val PERCENT_OF_QUALITY = 100
        private const val WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 1
        private const val TOAST_ERROR_MESSAGE = "Не удалось скачать изображение!"
    }
}
