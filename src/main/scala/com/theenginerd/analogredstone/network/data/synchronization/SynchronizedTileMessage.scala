/*
 * Copyright 2014 Joshua R. Rodgers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================================
 */

package com.theenginerd.analogredstone.network.data.synchronization

import io.netty.buffer.ByteBuf
import cpw.mods.fml.common.FMLLog
import com.theenginerd.analogredstone.network.data.PropertyTypeIds

class SynchronizedTileMessage(var x: Int, var y: Int, var z: Int, var properties: Seq[Property]) extends SynchronizedMessage
{
    def this() = this(0, 0, 0, List())

    def writeToBuffer(buffer: ByteBuf) =
    {
        buffer.writeInt(x)
        buffer.writeInt(y)
        buffer.writeInt(z)

        for(property <- properties)
        {
            buffer.writeByte(property.id)
            buffer.writeByte(property.typeId)
            writePropertyToBuffer(property, buffer)
        }
    }

    def writePropertyToBuffer(property: Property, buffer: ByteBuf): Any =
    {
        property.value match
        {
            case value: Boolean =>
                buffer.writeBoolean(value)

            case value: Byte =>
                buffer.writeByte(value)

            case value: Short =>
                buffer.writeShort(value)

            case value: Int =>
                buffer.writeInt(value)

            case value: Float =>
                buffer.writeFloat(value)

            case unexpected =>
                val typ = unexpected.getClass
                FMLLog warning s"Unexpected serialization type found: $typ."
        }
    }

    def readFromBuffer(buffer: ByteBuf) =
    {
        x = buffer.readInt()
        y = buffer.readInt()
        z = buffer.readInt()

        while(buffer.readableBytes() > 0)
        {
            val propertyId = buffer.readByte()
            val propertyType = buffer.readByte()
            val propertyValue = readPropertyFromBuffer(propertyType, buffer)

            properties :+= new Property(propertyId, propertyType, propertyValue)
        }
    }

    def readPropertyFromBuffer(propertyType: Byte, buffer: ByteBuf): AnyVal =
    {
        import PropertyTypeIds._

        propertyType match
        {
            case BOOLEAN_ID => buffer.readBoolean()
            case BYTE_ID => buffer.readByte()
            case SHORT_ID => buffer.readShort()
            case INT_ID => buffer.readInt()
            case FLOAT_ID => buffer.readFloat()
        }
    }
}