/*
 * Copyright 2020 SpotBugs plugin contributors
 *
 * This file is part of IntelliJ SpotBugs plugin.
 *
 * IntelliJ SpotBugs plugin is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * IntelliJ SpotBugs plugin is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with IntelliJ SpotBugs plugin.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package org.jetbrains.plugins.spotbugs.actions;

import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.HTMLBugReporter;
import net.sf.saxon.TransformerFactoryImpl;
import org.dom4j.Document;
import org.dom4j.io.DocumentSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.spotbugs.common.util.IoUtil;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HtmlBugCollectionExporter implements BugCollectionExporter {

    private static final String FINDBUGS_PLAIN_XSL = "plain.xsl";

    public void export(@NotNull final BugCollection bugCollection, @NotNull final File file) throws IOException, TransformerException {
        final Document document = bugCollection.toDocument();
        final InputStream stylesheet = getStylesheetStream(FINDBUGS_PLAIN_XSL);
        try {
            final Source xsl = new StreamSource(stylesheet);
            xsl.setSystemId(FINDBUGS_PLAIN_XSL);

            // Create a transformer using the stylesheet
            final TransformerFactoryImpl transformerFactory = new TransformerFactoryImpl();
            final Transformer transformer = transformerFactory.newTransformer(xsl);

            // Source document is the XML generated from the BugCollection
            final Source source = new DocumentSource(document);

            // Write result to output stream
            final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            try {
                final Result result = new StreamResult(writer);
                // Do the transformation
                transformer.transform(source, result);
            } finally {
                IoUtil.safeClose(writer);
            }
        } finally {
            IoUtil.safeClose(stylesheet);
        }
    }

    @NotNull
    static InputStream getStylesheetStream(@NotNull final String stylesheet) throws IOException {
        try {
            final URL url = new URL(stylesheet);
            return url.openStream();
        } catch (final Exception e) {
            LOGGER.info("xls read failed.", e);
        }
        try {
            return new BufferedInputStream(new FileInputStream(stylesheet));
        } catch (final Exception ignored) {
        }
        final InputStream xslInputStream = HTMLBugReporter.class.getResourceAsStream('/' + stylesheet);
        if (xslInputStream == null) {
            throw new IOException("Could not load HTML generation stylesheet " + stylesheet);
        }
        return xslInputStream;
    }
}
