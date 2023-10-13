package com.ethernet389.mai.matrix_extensions

import Jama.Matrix
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(ExperimentalSerializationApi::class)
class MatrixSerializer : KSerializer<Matrix> {
    private val delegatedSerializer = ArraySerializer(DoubleArraySerializer())
    override val descriptor = SerialDescriptor("Matrix", delegatedSerializer.descriptor)

    override fun deserialize(decoder: Decoder): Matrix {
        val doubleArrays = decoder.decodeSerializableValue(delegatedSerializer)
        return Matrix(doubleArrays)
    }

    override fun serialize(encoder: Encoder, value: Matrix) {
        return encoder.encodeSerializableValue(delegatedSerializer, value.array)
    }
}