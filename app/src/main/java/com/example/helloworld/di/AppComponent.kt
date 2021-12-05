package com.example.helloworld.di

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ViewModelModule::class])
interface AppComponent {
   fun viewModelFactory(): MainViewModelFactory
}