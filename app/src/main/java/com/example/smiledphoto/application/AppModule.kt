package com.example.smiledphoto.application

import com.example.smiledphoto.data.preferences.Preferences
import com.example.smiledphoto.data.preferences.SharedPreferences
import com.example.smiledphoto.ui.gallery.GalleryDialogViewModel
import com.example.smiledphoto.ui.main.MainViewModel
import com.example.smiledphoto.ui.photo_dialog.PhotoDialogViewModel
import com.example.smiledphoto.ui.settings_dialog.SettingsDialogViewModel
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
