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
package org.netbeans.modules.xml.svg;

import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.awt.UndoRedo;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.w3c.dom.svg.SVGDocument;

/**
 *
 * @author Chris
 */
@MultiViewElement.Registration(
    displayName = "#LBL_SVGViewer",
    iconBase = "org/netbeans/modules/xml/resources/xmlObject.gif",
    mimeType = "image/svg+xml",
    persistenceType = TopComponent.PERSISTENCE_NEVER,
    preferredID = "SVGViewer",
    position = 1100
)
@Messages("LBL_SVGViewer=Preview")
public class SVGViewerElement implements MultiViewElement {

    private static final Logger LOG = Logger.getLogger(SVGViewerElement.class.getName());

    private final SVGDataObject dataObject;
    private transient JToolBar toolbar;

    private transient JComponent component;
    private transient JPanel viewer;

    private final FileChangeListener fcl = new FileChangeAdapter() {
        @Override
        public void fileChanged(FileEvent fe) {
            updateView();
        }

    };

    public SVGViewerElement(Lookup lookup) {
        dataObject = lookup.lookup(SVGDataObject.class);
    }

    @Override
    public JComponent getVisualRepresentation() {
        if (component == null) {
            viewer = new JPanel();
            component = viewer;
        }
        return component;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        if (toolbar == null) {
            toolbar = new JToolBar();
        }
        return toolbar;
    }

    @Override
    public Action[] getActions() {
        return new Action[0];
    }

    @Override
    public Lookup getLookup() {
        return dataObject.getLookup();
    }

    @Override
    public void componentOpened() {
        dataObject.getPrimaryFile().addFileChangeListener(fcl);
        updateView();
    }

    @Override
    public void componentClosed() {
        dataObject.getPrimaryFile().removeFileChangeListener(fcl);
    }

    @Override
    public void componentShowing() {
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
    }

    @Override
    public void componentDeactivated() {
    }

    @Override
    public UndoRedo getUndoRedo() {
        return UndoRedo.NONE;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    private JSVGCanvas svgCanvas = new JSVGCanvas();

    @Messages("TXT_MarkdownViewerElement_Error=<html>Something happened during markdown parsing.")
    private void updateView() {
        FileObject fo = dataObject.getPrimaryFile();
        if ((fo != null) && (viewer != null)) {
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
            String uri = "https://upload.wikimedia.org/wikipedia/commons/b/bd/Test.svg";
//            String uri = fo.getPath();

            try {
                SVGDocument doc = (SVGDocument) f.createDocument(uri);
                viewer.add(doc.getRootElement().);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
