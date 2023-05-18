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
import com.bolunevdev.core_api.entity.Film
import com.bolunevdev.kinon.R
import com.bolunevdev.kinon.databinding.FragmentDetailsBinding
import com.bolunevdev.kinon.utils.AutoDisposable
import com.bolunevdev.kinon.utils.addTo
import com.bolunevdev.kinon.view.activities.MainActivity
import com.bolunevdev.kinon.viewmodel.DetailsFragmentViewModel
import com.bolunevdev.remote_module.ApiConstants
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*


class DetailsFragment : Fragment() {

    private val viewModel: DetailsFragmentViewModel by activityViewModels()
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private var film: Film? = null
    private var favoriteFilms = mutableListOf<Film>()
    private val autoDisposable = AutoDisposable()

    init {
        enterTransition = Fade(Fade.IN).apply { duration = MainActivity.TRANSITION_DURATION }
        returnTransition = Fade(Fade.OUT).apply { duration = MainActivity.TRANSITION_DURATION }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity)
            .findViewById<FrameLayout>(R.id.backgroundShareTransition)
            .visibility = View.VISIBLE

        bindAutoDisposable()

        loadFilmsDataBase()

        initSharedElementEnterTransition()

        setDetails()

        initDetailsFabDownloadWp()

        initDetailsFabShare()

        initDetailsFabWatchLater()
    }

    private fun bindAutoDisposable() {
        autoDisposable.bindTo(lifecycle)
    }

    private fun loadFilmsDataBase() {
        viewModel.filmsListLiveData.observe(viewLifecycleOwner) { films ->
            favoriteFilms = films.toMutableList()
            initDetailsFabFavorites()
            setFabFavoritesIcon()
        }
    }

    private fun initDetailsFabFavorites() {
        binding.detailsFabFavorites.setOnClickListener {
            Completable.fromAction {
                if (!favoriteFilms.contains(film)) {
                    film?.let { it1 -> viewModel.addToFavoritesFilms(it1) }
                } else {
                    film?.let { it1 -> viewModel.deleteFromFavoritesFilms(it1.id) }
                }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorComplete()
                .subscribe()
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
                "${getString(R.string.CheckOutThisFilm)} ${film?.title} \n\n ${film?.description}"
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
        binding.detailsPoster.transitionName = film?.filmId.toString()
        binding.detailsToolbar.title = film?.title

        //Устанавливаем картинку
        Glide.with(this)
            .load(ApiConstants.IMAGES_URL + IMAGE_SIZE + film?.poster)
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
        binding.detailsDescription.text = film?.description
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
                put(MediaStore.Images.Media.TITLE, film?.title?.handleSingleQuote())
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    film?.title?.handleSingleQuote()
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
                film?.title?.handleSingleQuote(),
                film?.description?.handleSingleQuote()
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

        viewModel.loadWallpaper(ApiConstants.IMAGES_URL + "original" + film?.poster)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                // Включаем Прогресс-бар
                binding.progressBar.isVisible = true
            }
            .doFinally {
                //Отключаем Прогресс-бар
                binding.progressBar.isVisible = false
            }
            .subscribe({ bitmap ->
                //Сохраняем в галерею, как только файл загрузится
                saveToGallery(bitmap)
                // Выводим снекбар с кнопкой перейти в галерею
                Snackbar.make(
                    binding.root,
                    R.string.downloaded_to_gallery,
                    Snackbar.LENGTH_LONG
                )
                    .setAction(R.string.open) {
                        val intent = Intent().apply {
                            action = Intent.ACTION_VIEW
                            type = "image/*"
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    }
                    .show()
            }, {
                // Обрабатываем ошибку
                Toast.makeText(
                    requireContext(),
                    R.string.toast_error_load_message,
                    Toast.LENGTH_SHORT
                ).show()
            }).addTo(autoDisposable)
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

    private fun initDetailsFabWatchLater() {
        binding.detailsFabWatchLater.setOnClickListener {
            context?.let { it1 -> viewModel.createAlarm(it1, film as Film) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val MIME_TYPE = "text/plain"
        private const val IMAGE_SIZE = "w780"
        private const val MILLIS_TO_SEC_RATIO = 1000
        private const val IMAGE_TYPE = "image/jpeg"
        private const val DOWNLOAD_PATH = "Pictures/Kinon"
        private const val PERCENT_OF_QUALITY = 100
        private const val WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 1
    }
}
