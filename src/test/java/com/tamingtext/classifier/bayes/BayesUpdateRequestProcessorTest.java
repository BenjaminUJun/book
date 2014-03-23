/*
 * Copyright 2008-2011 Grant Ingersoll, Thomas Morton and Drew Farris
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * -------------------
 * To purchase or learn more about Taming Text, by Grant Ingersoll, Thomas Morton and Drew Farris, visit
 * http://www.manning.com/ingersoll
 */

package com.tamingtext.classifier.bayes;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tamingtext.TTTestCaseJ4;


public class BayesUpdateRequestProcessorTest extends TTTestCaseJ4 {
  
  static File baseDir;
  static File solrDir;
  
  @BeforeClass
  public static void beforeClass() throws Exception {
    /*
    baseDir = new File("target/test-output/update-test");
    if (!FileUtils.deleteQuietly(baseDir)) {
      Assert.fail("Could not delete " + baseDir);
    }

    if (!baseDir.mkdirs()) {
      Assert.fail("Count not create " + baseDir);
    }

    solrDir = new File(baseDir, "solr");
    solrDir.mkdirs();
    */
    initCore(
        "solr/conf/bayes-update-config.xml", 
        "solr/conf/bayes-schema.xml",
        "");
  }
  
  @Test
  public void testUpdate() {
    assertU("Add a doc to be classified",
        adoc("id",  "1234",
            "details", "Star Wars: The Empire Strikes Back"));
    
    assertU("Add a doc to be classified",
        adoc("id",  "1235",
            "details", "Lord of the Rings: The Two Towers"));
    
    assertU(commit());

    assertQ("Couldn't find indexed scifi instance",
        req("details:Empire"), "//result[@numFound=1]", "//str[@name='id'][.='1234']");

    assertQ("Couldn't find indexed fantasy instance",
        req("details:Towers"), "//result[@numFound=1]", "//str[@name='id'][.='1235']");
    
    assertQ("Couldn't find classified scifi instance",
        req("subject:scifi"), "//result[@numFound=1]", "//str[@name='id'][.='1234']");

    assertQ("Couldn't find classified fantasy instance",
        req("subject:fantasy"), "//result[@numFound=1]", "//str[@name='id'][.='1235']");
  }
}
