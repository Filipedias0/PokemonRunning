package com.example.pokedexapp

import androidx.test.filters.MediumTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before

@HiltAndroidTest
@MediumTest
@ExperimentalCoroutinesApi
class PokedexInstrumentedTests {
    var hiltRule = HiltAndroidRule(this)
    
    @Before
    fun setup(){
        hiltRule.inject()
    }
}