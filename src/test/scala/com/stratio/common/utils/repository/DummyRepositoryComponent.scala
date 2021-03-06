/**
 * Copyright (C) 2015 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stratio.common.utils.repository

import com.stratio.common.utils.repository.RepositoryComponent

trait DummyRepositoryComponent extends RepositoryComponent[String, String] {

  val repository: Repository = new DummyRepository()

  var memoryMap: Map[String, Any] = Map(
    "key1" -> "value1",
    "key2" -> "value2",
    "key3" -> Map("key31" -> "value31")
  )

  class DummyRepository() extends Repository {

    def get(id: String): Option[String] =
      memoryMap.get(id).map(_.toString)

    def getChildren(id: String): List[String] =
      memoryMap.get(id) match {
        case Some(map: Map[String, Any]@unchecked) => map.keys.toList
        case _ => List.empty[String]
      }

    def exists(id: String): Boolean =
      memoryMap.contains(id)

    def create(id: String, element: String): String = {
      if (!exists(id)) {
        memoryMap = memoryMap + (id -> element)
      }
      element
    }

    def update(id: String, element: String): Unit =
      if (exists(id)) memoryMap = memoryMap + (id -> element)

    def delete(id: String): Unit =
      if (exists(id)) memoryMap = memoryMap - id

    def getConfig: Map[String, Any] = memoryMap

    def start: Boolean = true

    def stop: Boolean = false

    def getState: RepositoryState = Started
  }
}
