/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.netbeans.modules.rust.grammar.lsp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.modules.lsp.client.spi.LanguageServerProvider;
import org.netbeans.modules.rust.options.api.RustAnalyzerOptions;
import org.openide.util.Lookup;

@MimeRegistration(mimeType = "text/x-rust", service = LanguageServerProvider.class)
public class RustLSP implements LanguageServerProvider {

    private static final Logger LOG = Logger.getLogger(RustLSP.class.getName());

    @Override
    public LanguageServerDescription startServer(Lookup lookup) {
        Path rustAnalyzerPath = RustAnalyzerOptions.getRustAnalyzerLocation(true);
        if(rustAnalyzerPath == null || ! Files.isExecutable(rustAnalyzerPath)) {
            return null;
        }
        try {
            Process p = new ProcessBuilder(new String[]{rustAnalyzerPath.toAbsolutePath().toString()})
                    .directory(rustAnalyzerPath.getParent().toFile())
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start();
            return LanguageServerDescription.create(p.getInputStream(), p.getOutputStream(), p);
        } catch (IOException ex) {
            LOG.log(Level.FINE, null, ex);
            return null;
        }
    }
}
