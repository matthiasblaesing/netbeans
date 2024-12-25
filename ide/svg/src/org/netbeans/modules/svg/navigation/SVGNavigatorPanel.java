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
package org.netbeans.modules.svg.navigation;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.SVGLoader;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.modules.svg.SVGDataObject;
import org.netbeans.modules.svg.SVGViewerElement;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.*;

/**
 *
 * @author christian lenz
 */
@NavigatorPanel.Registrations({
    @NavigatorPanel.Registration(mimeType = "image/svg+xml", displayName = "#Navigator_DisplayName")
})
public class SvgNavigatorPanel implements NavigatorPanel {

    private static final Logger LOG = Logger.getLogger(SVGViewerElement.class.getName());

    /**
     * holds UI of this panel
     */
    private SvgPreviewPanel panelUI;
    /**
     * template for finding data in given context. Object used as example,
     * replace with your own data source, for example JavaDataObject etc
     */
    private static final Lookup.Template<SVGDataObject> MY_DATA = new Lookup.Template<>(SVGDataObject.class);
    /**
     * current context to work on
     */
    private Lookup.Result<SVGDataObject> currentContext;
    /**
     * listener to context changes
     */
    private LookupListener contextListener;
    /**
     * Listens for changes on image file.
     */
    private FileChangeListener fileChangeListener;
    private long lastSaveTime = -1;
    private DataObject currentDataObject;
    private static final RequestProcessor WORKER = new RequestProcessor(SvgNavigatorPanel.class.getName());

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(SvgNavigatorPanel.class, "Navigator_DisplayName");
    }

    @Override
    public String getDisplayHint() {
        return NbBundle.getMessage(SvgNavigatorPanel.class, "Navigator_DisplayHint");
    }

    @Override
    public JComponent getComponent() {
        if (lastSaveTime == -1) {
            lastSaveTime = System.currentTimeMillis();
        }
        if (panelUI == null) {
            panelUI = new SvgPreviewPanel();
        }
        return panelUI;
    }

    @Override
    public void panelActivated(Lookup context) {
        // lookup context and listen to result to get notified about context changes
        currentContext = context.lookup(MY_DATA);
        currentContext.addLookupListener(getContextListener());
        // get actual data and recompute content
        Collection<? extends SVGDataObject> data = currentContext.allInstances();
        currentDataObject = getDataObject(data);

        if (currentDataObject == null) {
            return;
        }

        if (fileChangeListener == null) {
            fileChangeListener = new SvgFileChangeAdapter();
        }
        currentDataObject.getPrimaryFile().addFileChangeListener(fileChangeListener);
        setNewContent(currentDataObject);
    }

    @Override
    public void panelDeactivated() {
        currentContext.removeLookupListener(getContextListener());
        currentContext = null;
        if (currentDataObject != null) {
            currentDataObject.getPrimaryFile().removeFileChangeListener(fileChangeListener);
        }
        currentDataObject = null;
    }

    @Override
    public Lookup getLookup() {
        // go with default activated Node strategy
        return null;
    }

    private void setNewContent(final DataObject dataObject) {
        if (dataObject == null) {
            return;
        }

        WORKER.post(() -> {
            FileObject fo = dataObject.getPrimaryFile();

            if (fo == null) {
                return;
            }

            if (panelUI == null) {
                return;
            }

            try {
                SVGLoader svgLoader = new SVGLoader();
                SVGDocument svgDocument = svgLoader.load(fo.toURL());

                SwingUtilities.invokeLater(() -> panelUI.setSvg(svgDocument));
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, ex.getMessage());
            }
        });
    }

    private DataObject getDataObject(Collection<? extends SVGDataObject> data) {
        if (data.isEmpty()) {
            return null;
        } else {
            return data.iterator().next();
        }
    }

    /**
     * Accessor for listener to context
     */
    private LookupListener getContextListener() {
        if (contextListener == null) {
            contextListener = new ContextListener();
        }
        return contextListener;
    }

    /**
     * Listens to changes of context and triggers proper action
     */
    private class ContextListener implements LookupListener {

        @Override
        public void resultChanged(LookupEvent ev) {
            currentDataObject = getDataObject(currentContext.allInstances());
            setNewContent(currentDataObject);
        }
    }

    private class SvgFileChangeAdapter extends FileChangeAdapter {

        @Override
        public void fileChanged(final FileEvent fe) {
            if (fe.getTime() > lastSaveTime) {
                lastSaveTime = System.currentTimeMillis();

                // Refresh image viewer
                SwingUtilities.invokeLater(() -> {
                    try {
                        currentDataObject = DataObject.find(fe.getFile());
                        setNewContent(currentDataObject);
                    } catch (DataObjectNotFoundException ex) {
                        Logger.getLogger(SvgNavigatorPanel.class.getName()).info(NbBundle.getMessage(SvgNavigatorPanel.class, "ERR_DataObject"));
                    }
                });
            }
        }
    }
}
