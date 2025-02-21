package com.sample.biometric.data

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.sample.biometric.proto.UserDataProto
import java.io.InputStream
import java.io.OutputStream

private const val DATA_STORE_FILE_NAME = "user_data.pb"

object UserAuthenticationSerializer : Serializer<UserDataProto> {

    override val defaultValue: UserDataProto = UserDataProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserDataProto {
        try {
            return UserDataProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", exception)
        }
    }

    override suspend fun writeTo(t: UserDataProto, output: OutputStream) = t.writeTo(output)

    val Context.userAuthenticationDataStore: DataStore<UserDataProto> by dataStore(
        fileName = DATA_STORE_FILE_NAME,
        serializer = UserAuthenticationSerializer
    )

}