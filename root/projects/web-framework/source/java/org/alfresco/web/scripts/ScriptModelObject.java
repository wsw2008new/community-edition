/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.util.ParameterCheck;
import org.alfresco.web.site.RequestContext;
import org.alfresco.web.site.model.ModelObject;

/**
 * @author muzquiano
 */
public final class ScriptModelObject implements Serializable
{
    private static final long serialVersionUID = -3378946227712939601L;

    protected RequestContext context;
    protected ModelObject modelObject;
    
    public ScriptModelObject(RequestContext context, ModelObject modelObject)
    {
        this.context = context;
        this.modelObject = modelObject;
    }

    public String getId()
    {
        return modelObject.getId();
    }

    public String getTitle()
    {
        return modelObject.getTitle();
    }

    public void setTitle(String value)
    {
        ParameterCheck.mandatory("value", value);
        modelObject.setTitle(value);
    }
    
    public String getDescription()
    {
        return modelObject.getDescription();
    }

    public void setDescription(String value)
    {
        ParameterCheck.mandatory("value", value);
        modelObject.setDescription(value);
    }

    public void save()
    {
        modelObject.save(context);
    }

    public void reload()
    {
        modelObject.reload(context);
    }

    public void remove()
    {
        modelObject.remove(context);
    }

    public String toXML()
    {
        return modelObject.toXML();
    }

    public boolean getBooleanProperty(String propertyName)
    {
        ParameterCheck.mandatory("propertyName", propertyName);
        return modelObject.getBooleanProperty(propertyName);
    }

    public String getProperty(String propertyName)
    {
        ParameterCheck.mandatory("propertyName", propertyName);
        return modelObject.getProperty(propertyName);
    }

    public void setProperty(String propertyName, String propertyValue)
    {
        ParameterCheck.mandatory("propertyName", propertyName);
        ParameterCheck.mandatory("propertyValue", propertyValue);
        modelObject.setProperty(propertyName, propertyValue);
    }

    public void removeProperty(String propertyName)
    {
        ParameterCheck.mandatory("propertyName", propertyName);
        modelObject.removeProperty(propertyName);
    }

    public long getModificationTime()
    {
        return modelObject.getModificationTime();
    }

    public void setModificationTime(long modificationTime)
    {
        ParameterCheck.mandatory("modificationTime", modificationTime);
        modelObject.setModificationTime(modificationTime);
    }

    public void touch()
    {
        modelObject.touch();
    }

    public String getRelativePath()
    {
        return modelObject.getRelativePath();
    }

    public String getFileName()
    {
        return modelObject.getFileName();
    }

    public String getRelativeFilePath()
    {
        return modelObject.getRelativeFilePath();
    }

    public Map getProperties()
    {
        ScriptableMap map = new ScriptableMap();

        Map properties = modelObject.getProperties();
        Iterator it = properties.keySet().iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            String value = (String) properties.get(key);
            map.put(key, value);
        }

        return map;
    }
    
    public Map getCustomProperties()
    {
    	ScriptableMap map = new ScriptableMap();
    	
        Map properties = modelObject.getCustomProperties();
        Iterator it = properties.keySet().iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            String value = (String) properties.get(key);
            map.put(key, value);
        }

        return map;
    }
}
