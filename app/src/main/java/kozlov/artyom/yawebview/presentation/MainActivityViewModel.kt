package kozlov.artyom.yawebview.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import kozlov.artyom.yawebview.utils.DataStoreRepository

class MainActivityViewModel(application: Application): AndroidViewModel(application) {
    private val repository = DataStoreRepository(application)

    val readFromDataStore = repository.readFromDataStore.asLiveData()

}