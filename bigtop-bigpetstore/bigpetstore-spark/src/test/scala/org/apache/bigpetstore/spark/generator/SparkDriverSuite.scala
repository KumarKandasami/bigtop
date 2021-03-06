/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.bigtop.bigpetstore.spark.generator

import Array._

import java.io.File
import java.nio.file.Files

import org.apache.spark.{SparkContext, SparkConf}

import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith


// hack for running tests with Gradle
@RunWith(classOf[JUnitRunner])
class SparkDriverSuite extends FunSuite {

  test("Generating data") {

    val tmpDir:File = Files.createTempDirectory("sparkDriverSuiteGeneratedData").toFile()
    // 10 stores, 1000 customers, 365.0 days
    val parameters:Array[String] = Array(tmpDir.toString(), "10", "1000", "365.0")

    SparkDriver.parseArgs(parameters)

    val conf = new SparkConf().setAppName("BPS Data Generator Test Suite").setMaster("local[2]")
    val sc = new SparkContext(conf)

    val transactionRDD = SparkDriver.generateData(sc)
    val transactionCount = transactionRDD.count()
    assert(transactionCount > 0)

    SparkDriver.writeData(transactionRDD)

    // check that generator wrote out the  data
    val transactionDir:File = new File(tmpDir, "transactions")
    assert(transactionDir.exists())
    assert(transactionDir.isDirectory())

    sc.stop()
  }
}
