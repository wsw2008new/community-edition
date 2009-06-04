/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * and Open Source Software ("FLOSS") applications as described in Alfresco's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing" */

package org.alfresco.repo.avm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.avm.AVMRemoteLocal;
import org.alfresco.repo.domain.PropertyValue;
import org.alfresco.service.cmr.avm.AVMException;
import org.alfresco.service.cmr.avm.AVMService;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.remote.AVMRemote;
import org.alfresco.service.namespace.QName;

/**
 * This takes a filesystem directory path and a repository path and name
 * and bulk loads recursively from the filesystem.
 * @author britt
 */
public class BulkLoader
{
    private AVMRemote fService;
    
    private int fPropertyCount = 0;
    
    /**
     * Create a new one.
     */
    public BulkLoader()
    {
    }

    /**
     * Set the AVMService.
     * @param service
     */
    public void setAvmService(AVMService service)
    {
        fService = new AVMRemoteLocal();
        ((AVMRemoteLocal)fService).setAvmService(service);
        
    }
    
    /**
     * Set the AVMService.
     * @param service
     */
    protected void setAvmRemoteService(AVMRemote service)
    {
        fService = service;
    }
    
    public void setPropertyCount(int propCount)
    {
        fPropertyCount = propCount;
    }
    
    /**
     * Recursively load content.
     * @param fsPath The path in the filesystem.
     * @param repPath
     */
    public void recursiveLoad(String fsPath, String repPath)
    {
        Map<QName, PropertyValue> props = new HashMap<QName, PropertyValue>();
        for (int i = 0; i < fPropertyCount; i++)
        {
            props.put(QName.createQName("silly", "prop" + i), new PropertyValue(DataTypeDefinition.TEXT, "I am property " + i));
        }
        File file = new File(fsPath);
        String name = file.getName();
        if (file.isDirectory())
        {
            fService.createDirectory(repPath, name);
            String[] children = file.list();
            String baseName = repPath.endsWith("/") ? repPath + name : repPath + "/" + name;
            fService.setNodeProperties(baseName, props);
            for (String child : children)
            {
                recursiveLoad(fsPath + "/" + child, baseName);
            }
        }
        else
        {
            try
            {
                InputStream in = new FileInputStream(file);
                OutputStream out = fService.createFile(repPath, name);
                fService.setNodeProperties(repPath + "/" + name, props);
                byte[] buff = new byte[8192];
                int read = 0;
                while ((read = in.read(buff)) != -1)
                {
                    out.write(buff, 0, read);
                }
                out.close();
                in.close();
            }
            catch (IOException e)
            {
                e.printStackTrace(System.err);
                throw new AVMException("I/O Error", e);
            }
        }
    }
}
