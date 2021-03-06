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

package com.stratio.common.utils.config

import java.io.File

import com.typesafe.config.{Config => TypesafeConfiguration, ConfigFactory}

import scala.collection.JavaConversions._
import scala.util.Try

trait TypesafeConfigComponent extends ConfigComponent {

  val config: Config = new TypesafeConfig()

  class TypesafeConfig(typeSafeConfig: Option[TypesafeConfiguration] = None,
                       file: Option[File] = None,
                       resource: Option[String] = None,
                       subPath: Option[String] = None) extends Config {

    val conf: TypesafeConfiguration =
      subPath.fold(
        resource.fold(
          file.fold(typeSafeConfig.getOrElse(ConfigFactory.load())) { externalFile =>
            val fileConfig = ConfigFactory.parseFile(externalFile)
            typeSafeConfig.fold(fileConfig) { tConfig =>
              tConfig.withFallback(fileConfig)
            }
          }) { typeSafeResource =>
          ConfigFactory.load(typeSafeResource)
        }
      ) { path =>
        resource.fold(
          file.fold(typeSafeConfig.getOrElse(ConfigFactory.load())) { externalFile =>
            val fileConfig = ConfigFactory.parseFile(externalFile)
            typeSafeConfig.fold(fileConfig) { tConfig =>
              tConfig.withFallback(fileConfig)
            }.getConfig(path)
          }) { typeSafeResource =>
          typeSafeConfig.fold(ConfigFactory.load(typeSafeResource)) { tConfig =>
            tConfig.withFallback(ConfigFactory.load(typeSafeResource))
          }.getConfig(path)
        }
      }

    def mergeConfig(typeSafeConfig: TypesafeConfiguration): Config =
      new TypesafeConfig(Option(conf.withFallback(typeSafeConfig)))

    def getSubConfig(subConfigKey: String): Option[Config] =
      Try {
        new TypesafeConfig(Option(conf.getConfig(subConfigKey)))
      }.toOption

    def getConfig(typeSafeConfig: TypesafeConfiguration,
                  file: Option[File] = None,
                  resource: Option[String] = None,
                  subPath: Option[String] = None): Option[Config] =
      Try {
        new TypesafeConfig(Option(typeSafeConfig), file, resource, subPath)
      }.toOption

    def getConfig(resource: String, subPath: String): Option[Config] =
      Try {
        new TypesafeConfig(None, None, Option(resource), Option(subPath))
      }.toOption

    def getConfig(resource: String): Option[Config] =
      Try {
        new TypesafeConfig(None, None, Option(resource), None)
      }.toOption

    def getConfig(file: File): Option[Config] =
      Try {
        new TypesafeConfig(None, Option(file))
      }.toOption

    def getConfig(file: File, subPath: String): Option[Config] =
      Try {
        new TypesafeConfig(None, Option(file), Option(subPath))
      }.toOption

    def getString(key: String): Option[String] =
      Try {
        conf.getString(key)
      }.toOption

    def getInt(key: String): Option[Int] =
      Try {
        conf.getInt(key)
      }.toOption

    def getStringList(key: String): List[String] =
      Try {
        conf.getStringList(key).toList
      }.getOrElse(List.empty[String])

    def toMap: Map[String, Any] = conf.root().toMap
  }

}
