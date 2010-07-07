/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.repo.domain.permissions;

import java.util.List;
import java.util.Map;

import org.alfresco.repo.security.permissions.ACEType;
import org.alfresco.repo.security.permissions.PermissionReference;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;

/**
 * This provides basic services such as caching, but defers to the underlying implementation
 * for CRUD operations.
 * 
 *     <b>alf_access_control_list</b>
 *     <b>alf_acl_member</b>
 *     <b>alf_acl_change_set</b>
 *     <b>alf_access_control_entry</b>
 *     <b>alf_ace_context</b>
 *     <b>alf_permission</b>
 *     <b>alf_authority</b>
 *     <b>alf_authority_alias</b>
 * 
 * @since 3.4
 * @author janv
 */
public interface AclCrudDAO
{
    //
    // Access Control List (ACL)
    //
    
    public Acl createAcl(AclEntity entity);
    public Acl getAcl(long aclEntityId);
    public AclUpdateEntity getAclForUpdate(long aclEntityId);
    public List<Long> getAclsThatInheritFromAcl(long aclEntityId);
    public Long getLatestAclByGuid(String aclGuid);
    public void updateAcl(AclUpdateEntity entity);
    public void deleteAcl(long aclEntityId);
    public List<Long> getADMNodesByAcl(long aclEntityId, int maxResults);
    public List<Long> getAVMNodesByAcl(long aclEntityId, int maxResults);
    //
    // Access Control Entry (ACE)
    //
    
    public Ace createAce(Permission permission, Authority authority, ACEType type, AccessStatus accessStatus);
    public Ace getAce(Permission permission, Authority authority, ACEType type, AccessStatus accessStatus);
    public Ace getAce(long aceEntityId);
    public Ace getOrCreateAce(Permission permission, Authority authority, ACEType type, AccessStatus accessStatus);
    public List<Ace> getAcesByAuthority(long authorityEntityId);
    public List<Map<String, Object>> getAcesAndAuthoritiesByAcl(long aclEntityId);
    public int deleteAces(List<Long> aceEntityIds);
    
    //
    // ACL Change Set
    //
    
    public long createAclChangeSet();
    public AclChangeSet getAclChangeSet(long aclChangeSetEntityId);
    public void deleteAclChangeSet(long aclChangeSetEntityId);
    
    //
    // ACL Member
    //
    
    public void addAclMembersToAcl(long aclId, List<Long> aceIds, int depth);
    public void addAclMembersToAcl(long aclId, List<Pair<Long, Integer>> aceIdsWithDepths);
    public List<AclMember> getAclMembersByAcl(long aclEntityId);
    public List<AclMemberEntity> getAclMembersByAclForUpdate(long aclEntityId);
    public List<AclMember> getAclMembersByAuthority(String authorityName);
    public void updateAclMember(AclMemberEntity entity);
    public int deleteAclMembers(List<Long> aclMemberIds);
    public int deleteAclMembersByAcl(long aclEntityId);
    
    //
    // Permission
    //
    
    public Permission createPermission(PermissionReference permissionReference);
    public Permission getPermission(long permissionEntityId);
    public Permission getPermission(PermissionReference permissionReference);
    public Permission getOrCreatePermission(PermissionReference permissionReference);
    public void renamePermission(QName oldTypeQName, String oldName, QName newTypeQName, String newName);
    public void deletePermission(long permissionEntityId);
    
    //
    // Authority
    //
    
    public Authority getAuthority(long authorityEntityId);
    public Authority getAuthority(String authorityName);
    public Authority getOrCreateAuthority(String authorityName);
    public void renameAuthority(String authorityNameBefore, String authorityAfter);
    public void deleteAuthority(long authorityEntityId);
    
    // AceContext     (NOTE: currently unused - intended for possible future enhancement)
    // AuthorityAlias (NOTE: currently unused - intended for possible future enhancement)
}
