/*
 * Copyright (C) 2016 Tencent WeChat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tencent.tinker.build.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


/**
 * The configuration properties.
 *
 * @author zhangshaowen
 */
public class TinkerMultidexConfigTask extends DefaultTask {
    static final String MULTIDEX_CONFIG_PATH = "build/intermediates/tinker_proguard/tinker_multidexkeep.pro"
    static final String MULTIDEX_CONFIG_SETTINGS =
            "-keep public class * implements com.tencent.tinker.loader.app.ApplicationLifeCycle {\n" +
                    "    *;\n" +
                    "}\n" +
                    "\n" +
                    "-keep public class * extends com.tencent.tinker.loader.TinkerLoader {\n" +
                    "    *;\n" +
                    "}\n" +
                    "\n" +
                    "-keep public class * extends com.tencent.tinker.loader.app.TinkerApplication {\n" +
                    "    *;\n" +
                    "}"


    def applicationVariant

    public TinkerMultidexConfigTask() {
        group = 'tinker'
    }

    @TaskAction
    def updateTinkerProguardConfig() {
        def file = project.file(MULTIDEX_CONFIG_PATH)
        project.logger.error("try update tinker multidex keep proguard file with ${file}")

        // Create the directory if it doesnt exist already
        file.getParentFile().mkdirs()

        // Write our recommended proguard settings to this file
        FileWriter fr = new FileWriter(file.path)

        fr.write(MULTIDEX_CONFIG_SETTINGS)
        fr.write("\n")
        //unlike proguard, if loader endwith *, we must change to **
        fr.write("#your dex.loader patterns here\n")
        Iterable<String> loader = project.extensions.tinkerPatch.dex.loader
        for (String pattern : loader) {
            if (pattern.endsWith("*")) {
                if (!pattern.endsWith("**")) {
                    pattern += "*"
                }
            }
            fr.write("-keep class " + pattern + " {\n" +
                    "    *;\n" +
                    "}\n")
            fr.write("\n")
        }
        fr.close()
    }


}