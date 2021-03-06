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

package com.theenginerd.core.common.synchronization

import com.theenginerd.core.common.network.synchronization.data.SynchronizedMessage

trait Synchronized extends MappedProperties
{
    protected def buildSynchronizedMessage(properties: Iterable[PropertyCell]): SynchronizedMessage
    protected def sendSynchronizedMessage(packet: => SynchronizedMessage)

    def handleSynchronizationMessage(message: SynchronizedMessage)
    {
        for(messageProperty <- message.getProperties)
        {
            for(property <- getPropertyById(messageProperty.id))
            {
                property := messageProperty.value.asInstanceOf[property.Value]
            }
        }
    }

    def synchronized(properties: MappedPropertyCell*)(handler: => Unit = {}): Unit =
    {
        handler
        sendSynchronizedMessage(buildSynchronizedMessage(properties))
    }
}
