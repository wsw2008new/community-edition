/*
 * #%L
 * Alfresco Sharepoint Protocol
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.module.vti.web.scripts;

import java.io.IOException;
import java.io.Writer;

import org.alfresco.repo.admin.SysAdminParams;
import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.json.JSONWriter;


/**
 * WebScript responsible for returning interesting Vti Server runtime parameters.
 * 
 * @author Mike Hatfield
 */
public class VtiServerWebScript extends AbstractWebScript
{
    private int vtiServerPort = 0;
    private String vtiServerHost = "${localname}";
    private String vtiServerProtocol = "http";
    private String contextPath;
    private SysAdminParams sysAdminParams;

    public void setPort(int vtiServerPort)
    {
        this.vtiServerPort = vtiServerPort;
    }

    public void setHost(String vtiServerHost)
    {
        this.vtiServerHost = vtiServerHost;
    }
    
    public void setProtocol(String vtiServerProtocol)
    {
        this.vtiServerProtocol = vtiServerProtocol;
    }
    
    public void setContextPath(String contextPath)
    {
        this.contextPath = contextPath;
    }

    public void setSysAdminParams(SysAdminParams sysAdminParams)
    {
        this.sysAdminParams = sysAdminParams;
    }

    /**
     * Execute the webscript and return the cached JavaScript response
     */
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        Writer writer = new StringBuilderWriter(8192);
        JSONWriter out = new JSONWriter(writer);
        try
        {
            out.startObject();
            out.writeValue("port", this.vtiServerPort);
            out.writeValue("host", this.sysAdminParams.subsituteHost(vtiServerHost));
            out.writeValue("protocol", this.vtiServerProtocol);
            out.writeValue("contextPath", contextPath);
            out.endObject();
        }
        catch (IOException jsonErr)
        {
            throw new WebScriptException("Error building response.", jsonErr);
        }

        res.getWriter().write(writer.toString());
        res.getWriter().flush();
        res.getWriter().close();
    }
}
