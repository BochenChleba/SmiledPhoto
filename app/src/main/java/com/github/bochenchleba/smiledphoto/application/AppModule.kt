package com.github.bochenchleba.smiledphoto.application

import com.github.bochenchleba.smiledphoto.data.preferences.Preferences
import com.github.bochenchleba.smiledphoto.data.preferences.SharedPreferences
import com.github.bochenchleba.smiledphoto.ui.gallery.GalleryDialogViewModel
import com.github.bochenchleba.smiledphoto.ui.main.MainViewModel
import com.github.bochenchleba.smiledphoto.ui.photo_dialog.PhotoDialogViewModel
import com.github.bochenchleba.smiledphoto.ui.settings_dialog.SettingsDialogViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<Preferences> { SharedPreferences(androidContext()) }
    viewModel { MainViewModel(get()) }
    viewModel { PhotoDialogViewModel() }
    viewModel { SettingsDialogViewModel(get()) }
    viewModel { GalleryDialogViewModel() }
}
