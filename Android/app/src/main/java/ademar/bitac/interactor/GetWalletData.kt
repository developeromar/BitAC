package ademar.bitac.interactor

import ademar.bitac.ext.observeBody
import ademar.bitac.model.Wallet
import ademar.bitac.repository.datasource.Cloud
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Retrofit
import javax.inject.Inject

class GetWalletData @Inject constructor(

        private val cloud: Cloud,
        private val retrofit: Retrofit

) {

    fun execute(wallet: Wallet): Single<Wallet> {
        return Observable.fromCallable {
            cloud.getAddressBalances(wallet.address)
        }.flatMap {
            retrofit.observeBody(it)
        }.filter {
            it.addresses != null
        }.map {
            it.addresses!!
        }.flatMapIterable {
            it
        }.map {
            wallet.copy(balance = it.balance ?: 0L)
        }.firstOrError()
    }

}
