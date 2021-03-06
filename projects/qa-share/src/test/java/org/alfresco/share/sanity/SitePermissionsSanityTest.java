/*
 * #%L
 * qa-share
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
package org.alfresco.share.sanity;

import static org.alfresco.po.share.enums.DataLists.CONTACT_LIST;
import static org.alfresco.po.share.site.calendar.CalendarPage.ActionEventVia.AGENDA_TAB;
import static org.alfresco.po.share.site.calendar.CalendarPage.ActionEventVia.DAY_TAB;
import static org.alfresco.po.share.site.calendar.CalendarPage.ActionEventVia.MONTH_TAB;
import static org.alfresco.po.share.site.calendar.CalendarPage.ActionEventVia.WEEK_TAB;
import static org.alfresco.po.share.site.document.DocumentAction.CHNAGE_TYPE;
import static org.alfresco.po.share.site.document.DocumentAction.COPY_TO;
import static org.alfresco.po.share.site.document.DocumentAction.DELETE_CONTENT;
import static org.alfresco.po.share.site.document.DocumentAction.DOCUMENT_INLINE_EDIT;
import static org.alfresco.po.share.site.document.DocumentAction.DOWNLOAD_DOCUMENT;
import static org.alfresco.po.share.site.document.DocumentAction.DOWNLOAD_FOLDER;
import static org.alfresco.po.share.site.document.DocumentAction.EDIT_OFFLINE;
import static org.alfresco.po.share.site.document.DocumentAction.EDIT_PROPERTIES;
import static org.alfresco.po.share.site.document.DocumentAction.MANAGE_ASPECTS;
import static org.alfresco.po.share.site.document.DocumentAction.MANAGE_PERMISSION_DOC;
import static org.alfresco.po.share.site.document.DocumentAction.MANAGE_PERMISSION_FOL;
import static org.alfresco.po.share.site.document.DocumentAction.MANAGE_RULES;
import static org.alfresco.po.share.site.document.DocumentAction.MOVE_TO;
import static org.alfresco.po.share.site.document.DocumentAction.START_WORKFLOW;
import static org.alfresco.po.share.site.document.DocumentAction.UPLOAD_DOCUMENT;
import static org.alfresco.po.share.site.document.DocumentAction.VIEW_IN_EXLPORER;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.dashlet.AddOnsRssFeedDashlet;
import org.alfresco.po.share.dashlet.ImagePreviewDashlet;
import org.alfresco.po.share.dashlet.RssFeedDashlet;
import org.alfresco.po.share.dashlet.SiteActivitiesDashlet;
import org.alfresco.po.share.dashlet.SiteDataListsDashlet;
import org.alfresco.po.share.dashlet.SiteLinksDashlet;
import org.alfresco.po.share.dashlet.SiteMembersDashlet;
import org.alfresco.po.share.dashlet.SiteSearchDashlet;
import org.alfresco.po.share.dashlet.WebViewDashlet;
import org.alfresco.po.share.dashlet.WikiDashlet;
import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.PendingInvitesPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteGroupsPage;
import org.alfresco.po.share.site.SiteMembersPage;
import org.alfresco.po.share.site.blog.BlogPage;
import org.alfresco.po.share.site.blog.PostViewPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.calendar.InformationEventForm;
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.po.share.site.datalist.NewListForm;
import org.alfresco.po.share.site.datalist.lists.ContactList;
import org.alfresco.po.share.site.discussions.DiscussionsPage;
import org.alfresco.po.share.site.discussions.TopicViewPage;
import org.alfresco.po.share.site.document.ConfirmDeletePage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.links.LinksPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.site.wiki.WikiPageList;
import org.alfresco.po.share.user.SelectActions;
import org.alfresco.po.share.user.TrashCanPage;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserProfile;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.WebDroneImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * This class includes Permissions Sanity tests
 * 
 * @author Marina.Nenadovets, Bogdan.Bocancea
 */
@Listeners(FailedTestListener.class)
public class SitePermissionsSanityTest extends AbstractUtils
{
    private static final Log logger = LogFactory.getLog(SitePermissionsSanityTest.class);
    String user1;
    String siteName;
    String uniqueRun = "TS" + System.currentTimeMillis();

    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName() + uniqueRun;
        logger.info("Start Tests in: " + testName);
        username = "user" + System.currentTimeMillis();
        siteName = "site" + System.currentTimeMillis();
        String user1 = username + "1";

        // creating 6 users
        for (int i = 1; i <= 6; i++)
        {
            CreateUserAPI.createEnterpriseUserAPI(drone, ADMIN_USERNAME, username + i);
        }

        // creating a group
        ShareUser.createEnterpriseGroup(drone, testName);

        // User1 logs in
        ShareUser.login(drone, user1);

        // User1 creates a public site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.openSiteDashboard(drone, siteName);

        // User1 adds all available dashlets
        CustomiseSiteDashboardPage customiseSiteDashboardPage = new CustomiseSiteDashboardPage(drone);
        SiteDashboardPage siteDashBoard = new SiteDashboardPage(drone);
        siteDashBoard.getSiteNav().selectCustomizeDashboard().render();
        customiseSiteDashboardPage.selectChangeLayout().selectNewLayout(4);
        customiseSiteDashboardPage.addAllDashlets();

        ShareUser.openSiteDashboard(drone, siteName);

        // User1 adds all available pages
        CustomizeSitePage customiseSitePage = new CustomizeSitePage(drone);
        ShareUser.customizeSite(drone, siteName).render();
        siteDashBoard = customiseSitePage.addAllPages().render();

        ShareUser.openSiteDashboard(drone, siteName);

        // User1 invites the users to the site with Manager, Collaborator, Contributor and Consumer roles
        //ShareUserMembers.inviteUserToSiteWithRole(drone, user1, username + "2", siteName, UserRole.MANAGER);
        //ShareUserMembers.inviteUserToSiteWithRole(drone, user1, username + "3", siteName, UserRole.COLLABORATOR);
        //ShareUserMembers.inviteUserToSiteWithRole(drone, user1, username + "4", siteName, UserRole.CONTRIBUTOR);
        //ShareUserMembers.inviteUserToSiteWithRole(drone, user1, username + "5", siteName, UserRole.CONSUMER);
        CreateUserAPI.inviteUserToSiteEnterpriseAPI(drone, user1, username + "2", siteName, UserRole.SITEMANAGER.getRoleName().replace(" ", ""));
        CreateUserAPI.inviteUserToSiteEnterpriseAPI(drone, user1, username + "3", siteName, UserRole.SITECOLLABORATOR.getRoleName().replace(" ", ""));
        CreateUserAPI.inviteUserToSiteEnterpriseAPI(drone, user1, username + "4", siteName, UserRole.SITECONTRIBUTOR.getRoleName().replace(" ", ""));
        CreateUserAPI.inviteUserToSiteEnterpriseAPI(drone, user1, username + "5", siteName, UserRole.SITECONSUMER.getRoleName().replace(" ", ""));

        // User1 logs out
        ShareUser.logout(drone);
    }

    /**
     * Check Manager permissions in Site
     */
    @Test(groups = "Sanity")
    public void AONE_15204() throws Exception
    {
        /** Start Test */
        testName = getClass().getSimpleName() + uniqueRun;
        // String siteName = getSiteName(testName);

        /** Test Data Setup */
        String user2 = username + "2";
        String user6 = username + "6";
        String group1 = testName;
        String itemForSearch = getFileName("manager") + System.currentTimeMillis();
        String fileName = getFileName("manager");
        String folderName = getFolderName("manager");
        testName += "manager";

        // User2 logs in
        ShareUser.login(drone, user2);

        // User2 opens Site Dashboard
        SiteDashboardPage dashBoard = ShareUser.openSiteDashboard(drone, siteName).render();
        String siteUrl = drone.getCurrentUrl();

        // data prep for Site Search dashlet
        ShareUser.openDocumentLibrary(drone);
        ShareUser.createContent(drone, new ContentDetails(itemForSearch), ContentType.PLAINTEXT);
        dashBoard = ShareUser.openSiteDashboard(drone, siteName).render();

        // Check whether links from Site Configuration Option are present
        SiteDashboardPage siteDashboardPage = new SiteDashboardPage(drone);
        assertTrue(siteDashboardPage.isCustomizeSiteDashboardLinkPresent());
        assertTrue(siteDashboardPage.isEditSiteDetailsLinkPresent());
        assertTrue(siteDashboardPage.isCustomizeSiteLinkPresent());
        assertTrue(siteDashboardPage.isLeaveSiteLinkPresent());

        // Check Configure option on Image preview dashlet
        ImagePreviewDashlet imagePreviewDashlet = siteDashboardPage.getDashlet(IMAGE_PREVIEW).render();
        assertTrue(imagePreviewDashlet.isConfigureIconDisplayed(), "The configure icon isn't available");

        // Check Configure option on Site Notice dashlet
        assertTrue(ShareUserDashboard.getSiteContentDashlet(drone, siteName).isConfigureIconDisplayed(), "Unable to locate configure icon");

        // Check Configure option on Wiki dashlet
        WikiDashlet wikiDashlet = siteDashboardPage.getDashlet(WIKI).render();
        assertTrue(wikiDashlet.isConfigureIconDisplayed(), "Configure icon isn't available");

        // Check Configure option on Web View dashlet
        WebViewDashlet webViewDashlet = siteDashboardPage.getDashlet(WEB_VIEW).render();
        assertTrue(webViewDashlet.isConfigureIconDisplayed(), "Configure icon isn't available");

        // Check Configure option on Saved Search dashlet
        assertTrue(ShareUserDashboard.getSavedSearchDashlet(drone).isConfigIconDisplayed(), "Configure icon isn't available");

        // Check Configure option on RSS Feed dashlet/Alfresco Add-ons News Feed
        RssFeedDashlet rssFeedDashlet = siteDashboardPage.getDashlet(RSS_FEED).render();
        assertTrue(rssFeedDashlet.isConfigureIconDisplayed(), "Configure icon isn't available");

        AddOnsRssFeedDashlet addOnsRssFeedDashlet = siteDashboardPage.getDashlet("addOns-rss").render();
        assertTrue(addOnsRssFeedDashlet.isConfigureIconDisplayed(), "Configure icon isn't available");

        // Verify Create Link and Link's Details on Site Link dashlet
        SiteLinksDashlet siteLinksDashlet = siteDashboardPage.getDashlet(SITE_LINKS).render();
        siteLinksDashlet.createLink(testName, new String("http://alfresco.com"));
        drone.navigateTo(siteUrl);
        assertTrue(siteLinksDashlet.isDetailsLinkDisplayed(), "Details link isn't available");

        // Verify New Topic link is displayed on My Discussions dashlet
        assertTrue(ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName).isNewTopicLinkDisplayed(), "Unable to find new topic link on the dashlet");

        // Verify Invite and All Members on Site Members dashlet are displayed
        SiteMembersDashlet siteMembersDashlet = siteDashboardPage.getDashlet(DASHLET_MEMBERS).render();
        assertTrue(siteMembersDashlet.isInviteLinkDisplayed(), "Unable to find Invite link");
        assertTrue(siteMembersDashlet.isAllMembersLinkDisplayed(), "Unable to find Invite link");

        // Verify Subscribe to RSS on Site Activities dashlet
        SiteActivitiesDashlet siteActivitiesDashlet = siteDashboardPage.getDashlet(SITE_ACTIVITIES).render();
        assertTrue(siteActivitiesDashlet.isRssBtnDisplayed(), "Unable to find RSS FEED button");

        // Verify Create Data List on Site Data Lists dashlet is displayed
        SiteDataListsDashlet siteDataListsDashlet = siteDashboardPage.getDashlet(DATA_LISTS).render();
        assertTrue(siteDataListsDashlet.isCreateDataListDisplayed(), "Create Data List link isn't available");

        // Verify Search on site search dashlet
        SiteSearchDashlet siteSearchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone).render();

        assertTrue(siteSearchDashlet.siteSearchWithRetry(itemForSearch), "Unable to use search from the dashlet");

        // Verify Help icon on all the dashlets
        assertTrue(siteDashboardPage.isHelpDisplayedForAllDashlets(), "The help balloon isn't available");

        // Navigate to Document Library, set any view as default
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        assertTrue(documentLibraryPage.getNavigation().isSetDefaultViewVisible());
        documentLibraryPage.getNavigation().selectSetCurrentViewToDefault();
        ViewType actualViewType = documentLibraryPage.getNavigation().getViewType();
        assertEquals(actualViewType, ViewType.DETAILED_VIEW, "The view isn't set to default");

        // Subscribe to RSS Feed
        documentLibraryPage.getNavigation().selectRssFeed(user2, DEFAULT_PASSWORD, siteName);

        // Check the page is actually RSS Feed page
        assertTrue(((WebDroneImpl) drone).getDriver().getPageSource().contains("Alfresco Share - Documents"), "The current url isn't RSS page");

        // Create content
        drone.navigateTo(siteUrl);
        ShareUser.openDocumentLibrary(drone).render();
        ContentDetails contentDetails = new ContentDetails(fileName);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, documentLibraryPage).render(maxWaitTime);

        // Add comment to the document
        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName).render();
        documentDetailsPage.addComment(testName);

        // Verify the comment is displayed
        assertTrue(documentDetailsPage.isCommentCorrect(testName));

        // Verify available actions for the document
        assertTrue(documentDetailsPage.isDocumentActionPresent(DOWNLOAD_DOCUMENT), "Download action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(EDIT_PROPERTIES), "Edit Properties action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(VIEW_IN_EXLPORER), "View in Browser isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(UPLOAD_DOCUMENT), "Upload New Version action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(DOCUMENT_INLINE_EDIT), "Inline Edit action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(EDIT_OFFLINE), "Edit Offline action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(COPY_TO), "Copy to action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(MOVE_TO), "Move to action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(DELETE_CONTENT), "Delete action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(START_WORKFLOW), "Start Workflow action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(MANAGE_PERMISSION_DOC), "Manage Permissions action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(MANAGE_ASPECTS), "Manage aspects action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(CHNAGE_TYPE), "Change type action isn't available");

        // Verify icons near document section
        assertTrue(documentDetailsPage.isEditPropertiesIconDisplayed(), "Edit properties icon isn't displayed");
        assertTrue(documentDetailsPage.isTagIconDisplayed(), "Edit tags icon isn't displayed");
        assertTrue(documentDetailsPage.isEditPermissionsIconDisplayed(), "Edit permissions icon isn't displayed");
        assertTrue(documentDetailsPage.isUploadNewVersionDisplayed(), "Upload new version icon isn't displayed");
        assertTrue(documentDetailsPage.isStartWorkflowIconDisplayed(), "Start Workflow icon isn't displayed");

        // Verify the following links are displayed: Favourite, Like, Comment, QuickShare
        assertTrue(documentDetailsPage.isSharePanePresent());
        assertTrue(documentDetailsPage.isFavouriteLinkPresent());
        assertTrue(documentDetailsPage.isLikeLinkPresent());
        assertTrue(documentDetailsPage.isCommentLinkPresent());
        ShareUser.openDocumentLibrary(drone);
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isShareLinkVisible());

        // Go back to doclib and create a folder
        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB_CONTAINER).render();

        // Open folder details and add a comment
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName).render();
        folderDetailsPage.addComment(testName).render();

        // Verify the available folder actions
        assertTrue(folderDetailsPage.isDocumentActionPresent(DOWNLOAD_FOLDER));
        assertTrue(folderDetailsPage.isDocumentActionPresent(EDIT_PROPERTIES));
        assertTrue(folderDetailsPage.isDocumentActionPresent(COPY_TO));
        assertTrue(folderDetailsPage.isDocumentActionPresent(MOVE_TO));
        assertTrue(folderDetailsPage.isDocumentActionPresent(MANAGE_RULES));
        assertTrue(folderDetailsPage.isDocumentActionPresent(DELETE_CONTENT));
        assertTrue(folderDetailsPage.isDocumentActionPresent(MANAGE_PERMISSION_FOL));
        assertTrue(folderDetailsPage.isDocumentActionPresent(MANAGE_ASPECTS));
        assertTrue(folderDetailsPage.isDocumentActionPresent(CHNAGE_TYPE));

        // Verify the panes are available
        assertTrue(folderDetailsPage.isSharePanePresent(), "Share pane isn't displayed for the folder");
        assertTrue(folderDetailsPage.isTagPanelPresent(), "Tags pane isn't displayed for the folder");
        assertTrue(folderDetailsPage.isPropertiesPanelPresent(), "Properties pane isn't displayed for the folder");
        assertTrue(folderDetailsPage.isPermissionsPanelPresent(), "Permissions pane isn't displayed for the folder");

        // Verify Edit controls are displayed near the panes
        assertTrue(folderDetailsPage.isTagIconDisplayed(), "Tag icon isn't displayed");
        assertTrue(folderDetailsPage.isEditPropertiesIconDisplayed(), "Edit properties icon isn't displayed");
        assertTrue(folderDetailsPage.isEditPermissionsIconDisplayed(), "Edit permissions icon isn't displayed");

        // Verify the following actions are present: Favourite, Like, Comment
        assertTrue(folderDetailsPage.isFavouriteLinkPresent(), "Favourite link isn't displayed for the folder");
        assertTrue(folderDetailsPage.isLikeLinkPresent(), "Like link isn't displayed for the folder");
        assertTrue(folderDetailsPage.isCommentLinkPresent(), "Comment link isn't displayed for the folder");

        // Navigate to Calendar
        CalendarPage calendarPage = dashBoard.getSiteNav().selectCalendarPage().render();

        // Create any event
        calendarPage.createEvent(testName, testName, testName, true).render();

        // Navigate to Wiki
        WikiPage wikiPage = dashBoard.getSiteNav().selectWikiPage().render();

        // Data prep for wiki
        List<String> txtLines = new ArrayList<>();
        txtLines.add(0, testName);

        // Create a wiki page
        wikiPage.createWikiPage(testName, txtLines).render();

        // Navigate to Discussions
        DiscussionsPage discussionsPage = dashBoard.getSiteNav().selectDiscussionsPage().render();

        // Create a topic
        TopicViewPage topicViewPage = discussionsPage.createTopic(testName, testName).render();
        discussionsPage = topicViewPage.clickBack().render();

        // Create a reply
        topicViewPage = discussionsPage.viewTopic(testName).render();
        topicViewPage.createReply(testName).render();

        // Navigate to Blog
        BlogPage blogPage = dashBoard.getSiteNav().selectBlogPage().render();

        // Data prep for blog
        String url = "alfrescoqacloud.wordpress.com";

        // Create a Blog post
        blogPage.createPostInternally(testName, testName).render();

        // Create a comment
        PostViewPage postViewPage = new PostViewPage(drone);
        postViewPage.createBlogComment(testName).render();

        // Browse to links page
        LinksPage linksPage = dashBoard.getSiteNav().selectLinksPage().render();

        // Create a link
        linksPage.createLink(testName, url).render();

        // Browse to Data Lists
        dashBoard.getSiteNav().selectDataListPage();
        DataListPage dataListPage = new DataListPage(drone).render();
        dataListPage.createDataList(CONTACT_LIST, testName, testName).render();

        // Item creation
        ContactList contactList = new ContactList(drone);
        dataListPage.selectDataList(testName);
        contactList.createItem(testName);

        // Duplicate any item
        contactList.duplicateAnItem(testName);

        // Navigate to Members and invite a user and a group
        ShareUserMembers.inviteUserToSiteWithRoleENT(drone, user2, user6, siteName, UserRole.COLLABORATOR);
        ShareUserMembers.inviteGroupToSiteWithRole(drone, user2, group1, siteName, UserRole.COLLABORATOR);
        SiteMembersPage siteMembersPage = dashBoard.getSiteNav().selectMembersPage();

        // Cancel the invitation
        PendingInvitesPage pendingInvitesPage = siteMembersPage.navigateToPendingInvites().render();
        pendingInvitesPage.cancelInvitation(user6);
    }

    /**
     * Check Collaborator permissions in Site
     */
    @Test(groups = "Sanity")
    public void AONE_15205() throws Exception
    {
        /* test data set up */
        testName = getClass().getSimpleName() + uniqueRun;
        // String siteName = getSiteName(testName);
        String fileName = getFileName("collaborator");
        String folderName = getFolderName("collaborator");
        String itemForSearch = getFileName("collaborator") + System.currentTimeMillis();
        String user3 = username + "3";
        testName += "collaborator";

        // User3 logs in
        ShareUser.login(drone, user3, DEFAULT_PASSWORD);

        // User3 opens Site Dashboard
        SiteDashboardPage dashBoard = ShareUser.openSiteDashboard(drone, siteName).render();
        String siteUrl = drone.getCurrentUrl();

        // data prep for Site Search dashlet
        ShareUser.openDocumentLibrary(drone).render();
        ShareUser.createContent(drone, new ContentDetails(itemForSearch), ContentType.PLAINTEXT).render();
        ShareUser.openSiteDashboard(drone, siteName).render();

        // Check whether links from Site Configuration Option are present
        SiteDashboardPage siteDashboardPage = new SiteDashboardPage(drone);
        Assert.assertFalse(siteDashboardPage.isCustomizeSiteDashboardLinkPresent());
        assertFalse(siteDashboardPage.isEditSiteDetailsLinkPresent());
        assertFalse(siteDashboardPage.isCustomizeSiteLinkPresent());
        assertTrue(siteDashboardPage.isLeaveSiteLinkPresent());

        SiteMembersDashlet siteMembersDashlet = siteDashboardPage.getDashlet(DASHLET_MEMBERS).render();
        assertFalse(siteMembersDashlet.isInviteLinkDisplayed(), "Unable to find Invite link");
        assertTrue(siteMembersDashlet.isAllMembersLinkDisplayed(), "Unable to find Invite link");

        // Verify Create Link and Link's Details on Site Link dashlet
        SiteLinksDashlet siteLinksDashlet = siteDashboardPage.getDashlet(SITE_LINKS).render();
        siteLinksDashlet.createLink(fileName, new String("http://alfresco.com"));
        drone.navigateTo(siteUrl);
        assertTrue(siteLinksDashlet.isDetailsLinkDisplayed(), "Details link isn't available");

        // Verify New Topic link is displayed on My Discussions dashlet
        assertTrue(ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName).isNewTopicLinkDisplayed(), "Unable to find new topic link on the dashlet");

        // Verify only All Members on Site Members dashlet is displayed

        // Verify Subscribe to RSS on Site Activities dashlet
        SiteActivitiesDashlet siteActivitiesDashlet = siteDashboardPage.getDashlet(SITE_ACTIVITIES).render();
        assertTrue(siteActivitiesDashlet.isRssBtnDisplayed(), "Unable to find RSS FEED button");

        // Verify Create Data List on Site Data Lists dashlet is displayed
        SiteDataListsDashlet siteDataListsDashlet = siteDashboardPage.getDashlet(DATA_LISTS).render();
        assertTrue(siteDataListsDashlet.isCreateDataListDisplayed(), "Create Data List link isn't available");

        // Verify Help icon on all the dashlets
        assertTrue(siteDashboardPage.isHelpDisplayedForAllDashlets(), "The help balloon isn't available");

        // Verify Search on site search dashlet
        SiteSearchDashlet siteSearchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone).render();
        assertTrue(siteSearchDashlet.siteSearchWithRetry(itemForSearch), "Unable to use search from the dashlet");

        // Navigate to Document Library, set any view as default
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        assertFalse(documentLibraryPage.getNavigation().isSetDefaultViewVisible(), "Set View to default is present");

        // Subscribe to RSS Feed
        documentLibraryPage.getNavigation().selectRssFeed(user3, DEFAULT_PASSWORD, siteName);

        // Check the page is actually RSS Feed page
        assertTrue(((WebDroneImpl) drone).getDriver().getPageSource().contains("Alfresco Share - Documents"), "The current url isn't RSS page");

        // Create content
        drone.navigateTo(siteUrl);
        ShareUser.openDocumentLibrary(drone).render();
        ContentDetails contentDetails = new ContentDetails(fileName);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, documentLibraryPage).render(maxWaitTime);

        // Add comment to the document
        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName);
        documentDetailsPage.addComment(testName).render();

        // Verify the comment is displayed
        assertTrue(documentDetailsPage.isCommentCorrect(testName));

        // Verify available actions for the document
        assertTrue(documentDetailsPage.isDocumentActionPresent(DOWNLOAD_DOCUMENT), "Download action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(EDIT_PROPERTIES), "Edit Properties action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(VIEW_IN_EXLPORER), "View in Browser isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(UPLOAD_DOCUMENT), "Upload New Version action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(DOCUMENT_INLINE_EDIT), "Inline Edit action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(EDIT_OFFLINE), "Edit Offline action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(COPY_TO), "Copy to action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(MOVE_TO), "Move to action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(DELETE_CONTENT), "Delete action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(START_WORKFLOW), "Start Workflow action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(MANAGE_PERMISSION_DOC), "Manage Permissions action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(MANAGE_ASPECTS), "Manage aspects action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(CHNAGE_TYPE), "Change type action isn't available");

        // Verify icons near document section
        assertTrue(documentDetailsPage.isEditPropertiesIconDisplayed(), "Edit properties icon isn't displayed");
        assertTrue(documentDetailsPage.isTagIconDisplayed(), "Edit tags icon isn't displayed");
        assertTrue(documentDetailsPage.isEditPermissionsIconDisplayed(), "Edit permissions icon isn't displayed");
        assertTrue(documentDetailsPage.isUploadNewVersionDisplayed(), "Upload new version icon isn't displayed");
        assertTrue(documentDetailsPage.isStartWorkflowIconDisplayed(), "Start Workflow icon isn't displayed");

        // Verify the following links are displayed: Favourite, Like, Comment, QuickShare
        assertTrue(documentDetailsPage.isSharePanePresent());
        assertTrue(documentDetailsPage.isFavouriteLinkPresent());
        assertTrue(documentDetailsPage.isLikeLinkPresent());
        assertTrue(documentDetailsPage.isCommentLinkPresent());
        ShareUser.openDocumentLibrary(drone).render();
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isShareLinkVisible());

        // Go back to doclib and create a folder
        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB_CONTAINER).render();

        // Open folder details and add a comment
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName).render();
        folderDetailsPage.addComment(testName).render();

        // Verify the available folder actions
        assertTrue(folderDetailsPage.isDocumentActionPresent(DOWNLOAD_FOLDER));
        assertTrue(folderDetailsPage.isDocumentActionPresent(EDIT_PROPERTIES));
        assertTrue(folderDetailsPage.isDocumentActionPresent(COPY_TO));
        assertTrue(folderDetailsPage.isDocumentActionPresent(MOVE_TO));
        assertTrue(folderDetailsPage.isDocumentActionPresent(MANAGE_RULES));
        assertTrue(folderDetailsPage.isDocumentActionPresent(DELETE_CONTENT));
        assertTrue(folderDetailsPage.isDocumentActionPresent(MANAGE_PERMISSION_FOL));
        assertTrue(folderDetailsPage.isDocumentActionPresent(MANAGE_ASPECTS));
        assertTrue(folderDetailsPage.isDocumentActionPresent(CHNAGE_TYPE));

        // Verify the panes are available
        assertTrue(folderDetailsPage.isSharePanePresent(), "Share pane isn't displayed for the folder");
        assertTrue(folderDetailsPage.isTagPanelPresent(), "Tags pane isn't displayed for the folder");
        assertTrue(folderDetailsPage.isPropertiesPanelPresent(), "Properties pane isn't displayed for the folder");
        assertTrue(folderDetailsPage.isPermissionsPanelPresent(), "Permissions pane isn't displayed for the folder");

        // Verify Edit controls are displayed near the panes
        assertTrue(folderDetailsPage.isTagIconDisplayed(), "Tag icon isn't displayed");
        assertTrue(folderDetailsPage.isEditPropertiesIconDisplayed(), "Edit properties icon isn't displayed");
        assertTrue(folderDetailsPage.isEditPermissionsIconDisplayed(), "Edit permissions icon isn't displayed");

        // Verify the following actions are present: Favourite, Like, Comment
        assertTrue(folderDetailsPage.isFavouriteLinkPresent(), "Favourite link isn't displayed for the folder");
        assertTrue(folderDetailsPage.isLikeLinkPresent(), "Like link isn't displayed for the folder");
        assertTrue(folderDetailsPage.isCommentLinkPresent(), "Comment link isn't displayed for the folder");

        // Navigate to Calendar
        CalendarPage calendarPage = dashBoard.getSiteNav().selectCalendarPage().render();

        // Create any event
        calendarPage.createEvent(testName, testName, testName, true).render();

        // Navigate to Wiki
        WikiPage wikiPage = dashBoard.getSiteNav().selectWikiPage().render();

        // Data prep for wiki
        List<String> txtLines = new ArrayList<>();
        txtLines.add(0, testName);

        // Create a wiki page
        wikiPage.createWikiPage(testName, txtLines).render();

        // Navigate to Discussions
        DiscussionsPage discussionsPage = dashBoard.getSiteNav().selectDiscussionsPage().render();

        // Create a topic
        TopicViewPage topicViewPage = discussionsPage.createTopic(testName, testName).render();
        discussionsPage = topicViewPage.clickBack().render();

        // Create a reply
        topicViewPage = discussionsPage.viewTopic(testName).render();
        topicViewPage.createReply(testName).render();

        // Navigate to Blog
        BlogPage blogPage = dashBoard.getSiteNav().selectBlogPage().render();

        // Data prep for blog
        String url = "alfrescoqacloud.wordpress.com";

        // Create a Blog post
        blogPage.createPostInternally(testName, testName).render();

        // Create a comment
        PostViewPage postViewPage = new PostViewPage(drone);
        postViewPage.createBlogComment(testName).render();

        // Browse to links page
        LinksPage linksPage = dashBoard.getSiteNav().selectLinksPage().render();

        // Create a link
        linksPage.createLink(testName, url).render();

        // Browse to Data Lists
        DataListPage dataListPage;
        try
        {
            dataListPage = dashBoard.getSiteNav().selectDataListPage().render();
        }
        catch (ClassCastException cce)
        {
            dataListPage = new DataListPage(drone);
        }

        // Create a list
        dataListPage.createDataList(CONTACT_LIST, testName, testName).render();

        // Item creation
        ContactList contactList = new ContactList(drone);
        dataListPage.selectDataList(testName);
        contactList.createItem(testName);

        // Duplicate any item
        contactList.duplicateAnItem(testName);

        // Browse to site Members
        SiteMembersPage siteMembersPage = dashBoard.getSiteNav().selectMembersPage().render();

        // Only People and Groups tags are present
        assertTrue(siteMembersPage.isPeopleLinkPresent(), "People link is not displayed");
        assertTrue(siteMembersPage.isGroupLinkPresent(), "Group link is not displayed");

        // It is not possible to invite users and groups
        assertFalse(siteMembersPage.isInviteLinkPresent(), "Invite link is present");
        SiteGroupsPage siteGroupsPage = siteMembersPage.navigateToSiteGroups();
        assertFalse(siteGroupsPage.isAddGroupDisplayed(), "Add Groups button is displayed");
    }

    /**
     * Check Contributor permissions in Site
     */
    @Test(groups = "Sanity")
    public void AONE_15206() throws Exception
    {
        /* test data set up */
        testName = getClass().getSimpleName() + uniqueRun;
        // String siteName = getSiteName(testName);
        String folderName = getFolderName("contributor");
        String fileName = getFileName("contributor");
        String itemForSearch = getFileName("contributor") + System.currentTimeMillis();
        String user4 = username + "4";
        testName += "contributor";
        String url = "google.com";

        // User3 logs in
        ShareUser.login(drone, user4, DEFAULT_PASSWORD);

        // User3 opens Site Dashboard
        SiteDashboardPage dashBoard = ShareUser.openSiteDashboard(drone, siteName).render();
        String siteUrl = drone.getCurrentUrl();

        // data prep for Site Search dashlet
        ShareUser.openDocumentLibrary(drone);
        ShareUser.createContent(drone, new ContentDetails(itemForSearch), ContentType.PLAINTEXT);
        ShareUser.openSiteDashboard(drone, siteName).render();

        // Check whether links from Site Configuration Option are present
        SiteDashboardPage siteDashboardPage = new SiteDashboardPage(drone);
        Assert.assertFalse(siteDashboardPage.isCustomizeSiteDashboardLinkPresent());
        assertFalse(siteDashboardPage.isEditSiteDetailsLinkPresent());
        assertFalse(siteDashboardPage.isCustomizeSiteLinkPresent());
        assertTrue(siteDashboardPage.isLeaveSiteLinkPresent());

        // Verify Create Link and Link's Details on Site Link dashlet
        SiteLinksDashlet siteLinksDashlet = siteDashboardPage.getDashlet(SITE_LINKS).render();
        siteLinksDashlet.createLink(fileName, new String("http://alfresco.com"));
        drone.navigateTo(siteUrl);
        assertTrue(siteLinksDashlet.isDetailsLinkDisplayed(), "Details link isn't available");

        // Verify New Topic link is displayed on My Discussions dashlet
        assertTrue(ShareUserDashboard.getMyDiscussionsDashlet(drone, siteName).isNewTopicLinkDisplayed(), "Unable to find new topic link on the dashlet");

        // Verify only All Members on Site Members dashlet is displayed
        SiteMembersDashlet siteMembersDashlet = siteDashboardPage.getDashlet(DASHLET_MEMBERS).render();
        assertFalse(siteMembersDashlet.isInviteLinkDisplayed(), "Unable to find Invite link");
        assertTrue(siteMembersDashlet.isAllMembersLinkDisplayed(), "Unable to find Invite link");

        // Verify Subscribe to RSS on Site Activities dashlet
        SiteActivitiesDashlet siteActivitiesDashlet = siteDashboardPage.getDashlet(SITE_ACTIVITIES).render();
        assertTrue(siteActivitiesDashlet.isRssBtnDisplayed(), "Unable to find RSS FEED button");

        // Verify Create Data List on Site Data Lists dashlet is displayed
        SiteDataListsDashlet siteDataListsDashlet = siteDashboardPage.getDashlet(DATA_LISTS).render();
        assertTrue(siteDataListsDashlet.isCreateDataListDisplayed(), "Create Data List link isn't available");

        // Verify Help icon on all the dashlets
        assertTrue(siteDashboardPage.isHelpDisplayedForAllDashlets(), "The help balloon isn't available");

        // Verify Search on site search dashlet
        SiteSearchDashlet siteSearchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone).render();
        assertTrue(siteSearchDashlet.siteSearchWithRetry(itemForSearch), "Unable to use search from the dashlet");

        // Navigate to Document Library, set any view as default
        ShareUser.openDocumentLibrary(drone).render();
        DocumentLibraryPage documentLibraryPage = new DocumentLibraryPage(drone).render();
        assertFalse(documentLibraryPage.getNavigation().isSetDefaultViewVisible(), "Set View to default is present");

        // Subscribe to RSS Feed
        documentLibraryPage.getNavigation().selectRssFeed(user4, DEFAULT_PASSWORD, siteName);

        // Check the page is actually RSS Feed page
        assertTrue(((WebDroneImpl) drone).getDriver().getPageSource().contains("Alfresco Share - Documents"), "The current url isn't RSS page");

        // Create content
        drone.navigateTo(siteUrl);
        ShareUser.openDocumentLibrary(drone).render();
        ContentDetails contentDetails = new ContentDetails(fileName);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, documentLibraryPage).render(maxWaitTime);

        // Add comment to the document
        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName);
        documentDetailsPage.addComment(testName).render();

        // Verify the comment is displayed
        assertTrue(documentDetailsPage.isCommentCorrect(testName));

        // Verify available actions for the document
        assertTrue(documentDetailsPage.isDocumentActionPresent(DOWNLOAD_DOCUMENT), "Download action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(EDIT_PROPERTIES), "Edit Properties action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(VIEW_IN_EXLPORER), "View in Browser isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(UPLOAD_DOCUMENT), "Upload New Version action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(DOCUMENT_INLINE_EDIT), "Inline Edit action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(EDIT_OFFLINE), "Edit Offline action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(COPY_TO), "Copy to action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(MOVE_TO), "Move to action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(DELETE_CONTENT), "Delete action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(START_WORKFLOW), "Start Workflow action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(MANAGE_PERMISSION_DOC), "Manage Permissions action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(MANAGE_ASPECTS), "Manage aspects action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(CHNAGE_TYPE), "Change type action isn't available");

        // Verify icons near document section
        assertTrue(documentDetailsPage.isEditPropertiesIconDisplayed(), "Edit properties icon isn't displayed");
        assertTrue(documentDetailsPage.isTagIconDisplayed(), "Edit tags icon isn't displayed");
        assertTrue(documentDetailsPage.isEditPermissionsIconDisplayed(), "Edit permissions icon isn't displayed");
        assertTrue(documentDetailsPage.isUploadNewVersionDisplayed(), "Upload new version icon isn't displayed");
        assertTrue(documentDetailsPage.isStartWorkflowIconDisplayed(), "Start Workflow icon isn't displayed");

        // Verify the following links are displayed: Favourite, Like, Comment, QuickShare
        assertTrue(documentDetailsPage.isSharePanePresent());
        assertTrue(documentDetailsPage.isFavouriteLinkPresent());
        assertTrue(documentDetailsPage.isLikeLinkPresent());
        assertTrue(documentDetailsPage.isCommentLinkPresent());
        ShareUser.openDocumentLibrary(drone);
        assertTrue(documentLibraryPage.getFileDirectoryInfo(fileName).isShareLinkVisible());

        // Go back to doclib and create a folder
        ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB_CONTAINER);

        // Open folder details and add a comment
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName).render();
        folderDetailsPage.addComment(testName);

        // Verify the available folder actions
        assertTrue(folderDetailsPage.isDocumentActionPresent(DOWNLOAD_FOLDER));
        assertTrue(folderDetailsPage.isDocumentActionPresent(EDIT_PROPERTIES));
        assertTrue(folderDetailsPage.isDocumentActionPresent(COPY_TO));
        assertTrue(folderDetailsPage.isDocumentActionPresent(MOVE_TO));
        assertTrue(folderDetailsPage.isDocumentActionPresent(MANAGE_RULES));
        assertTrue(folderDetailsPage.isDocumentActionPresent(DELETE_CONTENT));
        assertTrue(folderDetailsPage.isDocumentActionPresent(MANAGE_PERMISSION_FOL));
        assertTrue(folderDetailsPage.isDocumentActionPresent(MANAGE_ASPECTS));
        assertTrue(folderDetailsPage.isDocumentActionPresent(CHNAGE_TYPE));

        // Verify the panes are available
        assertTrue(folderDetailsPage.isSharePanePresent(), "Share pane isn't displayed for the folder");
        assertTrue(folderDetailsPage.isTagPanelPresent(), "Tags pane isn't displayed for the folder");
        assertTrue(folderDetailsPage.isPropertiesPanelPresent(), "Properties pane isn't displayed for the folder");
        assertTrue(folderDetailsPage.isPermissionsPanelPresent(), "Permissions pane isn't displayed for the folder");

        // Verify Edit controls are displayed near the panes
        assertTrue(folderDetailsPage.isTagIconDisplayed(), "Tag icon isn't displayed");
        assertTrue(folderDetailsPage.isEditPropertiesIconDisplayed(), "Edit properties icon isn't displayed");
        assertTrue(folderDetailsPage.isEditPermissionsIconDisplayed(), "Edit permissions icon isn't displayed");

        // Verify the following actions are present: Favourite, Like, Comment
        assertTrue(folderDetailsPage.isFavouriteLinkPresent(), "Favourite link isn't displayed for the folder");
        assertTrue(folderDetailsPage.isLikeLinkPresent(), "Like link isn't displayed for the folder");
        assertTrue(folderDetailsPage.isCommentLinkPresent(), "Comment link isn't displayed for the folder");

        // Navigate to Calendar
        CalendarPage calendarPage = dashBoard.getSiteNav().selectCalendarPage().render();

        // Create any event
        calendarPage.createEvent(MONTH_TAB, testName, testName, testName, null, null, null, null, null, false).render();

        // Navigate to Wiki
        WikiPage wikiPage = dashBoard.getSiteNav().selectWikiPage().render();

        // Data prep for wiki
        List<String> txtLines = new ArrayList<>();
        txtLines.add(0, testName);

        // Create a wiki page
        wikiPage.createWikiPage(testName, txtLines).render();

        // Navigate to Discussions
        DiscussionsPage discussionsPage = dashBoard.getSiteNav().selectDiscussionsPage().render();

        // Create a topic
        TopicViewPage topicViewPage = discussionsPage.createTopic(testName, testName).render();
        discussionsPage = topicViewPage.clickBack();

        // Create a reply
        topicViewPage = discussionsPage.viewTopic(testName).render();

        topicViewPage.createReply(testName).render();

        // Navigate to Blog
        BlogPage blogPage = dashBoard.getSiteNav().selectBlogPage().render();

        // Verify Configure External Blog is disabled
        // Removed Configure blog page since ACE-2094 and need to update the test

        // Create a Blog post
        blogPage.createPostInternally(testName, testName).render();

        // Create a comment
        PostViewPage postViewPage = new PostViewPage(drone);
        postViewPage.createBlogComment(testName).render();

        // Browse to links page
        LinksPage linksPage = dashBoard.getSiteNav().selectLinksPage().render();

        // Create a link
        linksPage.createLink(testName, url);

        // Browse to Data Lists
        DataListPage dataListPage;
        try
        {
            dataListPage = dashBoard.getSiteNav().selectDataListPage().render();
        }
        catch (ClassCastException cce)
        {
            dataListPage = new DataListPage(drone);
        }

        // Create a list
        dataListPage.createDataList(CONTACT_LIST, testName, testName).render();

        // Item creation
        ContactList contactList = new ContactList(drone);
        dataListPage.selectDataList(testName);
        contactList.createItem(testName);

        // Duplicate any item
        contactList.duplicateAnItem(testName);

        // Browse to site Members
        SiteMembersPage siteMembersPage = dashBoard.getSiteNav().selectMembersPage().render();

        // Only People and Groups tags are present
        assertTrue(siteMembersPage.isPeopleLinkPresent(), "People link is not displayed");
        assertTrue(siteMembersPage.isGroupLinkPresent(), "Group link is not displayed");

        // It is not possible to invite users and groups
        assertFalse(siteMembersPage.isInviteLinkPresent(), "Invite link is present");
        SiteGroupsPage siteGroupsPage = new SiteGroupsPage(drone);
        siteMembersPage.navigateToSiteGroups();
        assertFalse(siteGroupsPage.isAddGroupDisplayed(), "Add Groups button is displayed");
    }

    /**
     * Check Consumer permissions in Site
     */
    @Test(groups = "Sanity", dependsOnMethods = "AONE_15206")
    public void AONE_15207() throws Exception
    {
        /* test data set up */
        testName = getClass().getSimpleName() + uniqueRun;
        // String siteName = getSiteName(testName);
        String fileName = getFileName("contributor");
        String folderName = getFolderName("contributor");
        String user5 = username + "5";
        testName += "contributor";

        // User5 logs in
        ShareUser.login(drone, user5, DEFAULT_PASSWORD);

        // User5 opens Site Dashboard
        SiteDashboardPage dashBoard = ShareUser.openSiteDashboard(drone, siteName).render();
        String siteUrl = drone.getCurrentUrl();

        // Check whether links from Site Configuration Option are present
        SiteDashboardPage siteDashboardPage = new SiteDashboardPage(drone);
        Assert.assertFalse(siteDashboardPage.isCustomizeSiteDashboardLinkPresent());
        assertFalse(siteDashboardPage.isEditSiteDetailsLinkPresent());
        assertFalse(siteDashboardPage.isCustomizeSiteLinkPresent());
        assertTrue(siteDashboardPage.isLeaveSiteLinkPresent());

        // Verify Link's Details on Site Link dashlet are available
        SiteLinksDashlet siteLinksDashlet = siteDashboardPage.getDashlet(SITE_LINKS).render();
        assertTrue(siteLinksDashlet.isDetailsLinkDisplayed(), "Details link isn't available");

        // Verify Search on site search dashlet
        SiteSearchDashlet siteSearchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone).render();
        assertTrue(siteSearchDashlet.siteSearchWithRetry(fileName), "Unable to use search from the dashlet");

        // Verify only All Members on Site Members dashlet is displayed
        SiteMembersDashlet siteMembersDashlet = siteDashboardPage.getDashlet(DASHLET_MEMBERS).render();
        assertFalse(siteMembersDashlet.isInviteLinkDisplayed(), "Invite link is displayed");
        assertTrue(siteMembersDashlet.isAllMembersLinkDisplayed(), "Members link is displayed");

        // Verify Subscribe to RSS on Site Activities dashlet
        SiteActivitiesDashlet siteActivitiesDashlet = siteDashboardPage.getDashlet(SITE_ACTIVITIES).render();
        assertTrue(siteActivitiesDashlet.isRssBtnDisplayed(), "Unable to find RSS FEED button");

        // Verify Help icon on all the dashlets
        assertTrue(siteDashboardPage.isHelpDisplayedForAllDashlets(), "The help balloon isn't available");

        // Navigate to Document Library, set any view as default
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        assertFalse(documentLibraryPage.getNavigation().isSetDefaultViewVisible(), "Set View to default is present");

        // Subscribe to RSS Feed
        documentLibraryPage.getNavigation().selectRssFeed(user5, DEFAULT_PASSWORD, siteName);

        // Check the page is actually RSS Feed page
        assertTrue(((WebDroneImpl) drone).getDriver().getPageSource().contains("Alfresco Share - Documents"), "The current url isn't RSS page");

        drone.navigateTo(siteUrl);
        ShareUser.openDocumentLibrary(drone).render();

        // Verify impossibility to create a content
        assertFalse(documentLibraryPage.getNavigation().isCreateContentEnabled(), "The create content button is displayed");

        // Verify the impossibility to add a comment
        DocumentDetailsPage detailsPage = ShareUser.openDocumentDetailPage(drone, fileName);
        assertFalse(detailsPage.isAddCommentButtonDisplayed(), "Add comment button is available");

        // Verify available actions for the document
        assertTrue(detailsPage.isDocumentActionPresent(DOWNLOAD_DOCUMENT), "Download action isn't available");
        assertTrue(detailsPage.isDocumentActionPresent(VIEW_IN_EXLPORER), "View in Browser isn't available");
        assertTrue(detailsPage.isDocumentActionPresent(START_WORKFLOW), "Start Workflow action isn't available");

        // Verify icons near document section
        assertTrue(detailsPage.isStartWorkflowIconDisplayed(), "Start Workflow icon isn't visible");

        // Verify Download button is available
        assertTrue(detailsPage.isDownloadButtonVisible(), "Unable to find download button");

        // Verify the following links are displayed: Favourite, Like, Comment, QuickShare
        assertTrue(detailsPage.isFavouriteLinkPresent());
        assertTrue(detailsPage.isLikeLinkPresent());

        ShareUser.openDocumentLibrary(drone).render();
        // Verify it's not possible to create a folder
        assertFalse(documentLibraryPage.getNavigation().isNewFolderVisible(), "New folder button is displayed");

        // Verify it's not possible to add a comment to any folder
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName).render();
        assertFalse(folderDetailsPage.isAddCommentButtonDisplayed(), "Add comment button is displayed");

        // Verify the available folder actions for any folder
        assertTrue(folderDetailsPage.isDocumentActionPresent(DOWNLOAD_FOLDER), "Download as Zip option is not present");
        assertTrue(folderDetailsPage.isDocumentActionPresent(COPY_TO), "Copy to.. action isn't available");

        // There is no edit/manage icons near the folder sections
        assertTrue(folderDetailsPage.getEditControls().size() == 0, "There is edit/manage controls");

        // Verify the following actions are present: Favourite, Like
        assertTrue(folderDetailsPage.isFavouriteLinkPresent(), "Favourite link isn't displayed for the folder");
        assertTrue(folderDetailsPage.isLikeLinkPresent(), "Like link isn't displayed for the folder");

        // Verify it's not possible to create an event on any tab
        CalendarPage calendarPage = dashBoard.getSiteNav().selectCalendarPage().render();
        calendarPage.chooseDayTab().render();
        assertFalse(calendarPage.isAddEventPresent(), "Add event link is present");
        assertFalse(calendarPage.isAddEventClickable(DAY_TAB));

        calendarPage.chooseWeekTab().render();
        assertFalse(calendarPage.isAddEventPresent(), "Add event link is present");
        assertFalse(calendarPage.isAddEventClickable(WEEK_TAB));

        calendarPage.chooseMonthTab().render();
        assertFalse(calendarPage.isAddEventPresent(), "Add event link is present");
        assertFalse(calendarPage.isAddEventClickable(MONTH_TAB));

        calendarPage.chooseAgendaTab().render();
        assertFalse(calendarPage.isAddEventPresent(), "Add event link is present");
        assertFalse(calendarPage.isAddEventClickable(AGENDA_TAB));

        // Verify it's not possible to create a wiki page
        WikiPage wikiPage = dashBoard.getSiteNav().selectWikiPage().render();
        assertFalse(wikiPage.isNewPageDisplayed(), "New Page button is displayed");

        // Verify it's not possible to create any topic
        DiscussionsPage discussionsPage = dashBoard.getSiteNav().selectDiscussionsPage().render();
        assertFalse(discussionsPage.isNewTopicEnabled(), "New Topic button is present");

        // It's not possible to leave a reply
        TopicViewPage topicViewPage = discussionsPage.viewTopic(testName).render();
        assertFalse(topicViewPage.isReplyLinkDisplayed(), "Reply link is displayed");

        // Verify Configure External Blog is disabled
        BlogPage blogPage = dashBoard.getSiteNav().selectBlogPage().render();

        // Removed Configure blog page since ACE-2094 and need to update the test accordingly

        // It's not possible to create a post
        assertFalse(blogPage.isNewPostEnabled(), "New Post button is enabled");

        // It's not possible to add a comment
        PostViewPage postViewPage = blogPage.openBlogPost(testName).render();
        assertFalse(postViewPage.isAddCommentDisplayed(), "Add comment button is displayed");

        // It's not possible to create a link
        LinksPage linksPage = dashBoard.getSiteNav().selectLinksPage().render();
        assertFalse(linksPage.isCreateLinkEnabled(), "Create link button is visible");

        // Verify it's not possible to create a data list
        DataListPage dataListPage = (DataListPage) dashBoard.getSiteNav().selectDataListPage().render();
        assertFalse(dataListPage.isNewListEnabled(), "New List link is present");

        // Verify it's not possible to create/duplicate any item
        dataListPage.selectDataList(testName);
        ContactList contactList = new ContactList(drone);
        assertFalse(contactList.isNewItemEnabled(), "New item is enabled");
        assertFalse(contactList.isDuplicateDisplayed(testName), "Duplicate link is displayed");

        SiteMembersPage siteMembersPage = dashBoard.getSiteNav().selectMembersPage().render();

        // Only People and Groups tags are present
        assertTrue(siteMembersPage.isPeopleLinkPresent(), "People link is not displayed");
        assertTrue(siteMembersPage.isGroupLinkPresent(), "Group link is not displayed");

        // It is not possible to invite users and groups
        assertFalse(siteMembersPage.isInviteLinkPresent(), "Invite link is present");
        SiteGroupsPage siteGroupsPage = siteMembersPage.navigateToSiteGroups();
        assertFalse(siteGroupsPage.isAddGroupDisplayed(), "Add Groups button is displayed");
    }

    /**
     * Check Edit own content for Manager
     */
    @Test(groups = "Sanity", dependsOnMethods = "AONE_15204")
    public void AONE_15208() throws Exception
    {
        /** Start Test */
        testName = getClass().getSimpleName() + uniqueRun;
        // String siteName = getSiteName(testName);

        /** Test Data Setup */
        String user2 = username + 2;
        String user3 = username + 3;
        String group1 = testName;
        testName += "manager";
        String fileName = getFileName("manager");
        String folderName = getFolderName("manager");
        String fileNameEdited = fileName + "edited";
        String folderNameEdited = folderName + "edited";
        String editedItem = testName + "edited";

        // User2 logs in
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // User2 opens Site Dashboard
        SiteDashboardPage dashBoard = ShareUser.openSiteDashboard(drone, siteName).render();

        // Navigate to Document Library
        ShareUser.openDocumentLibrary(drone).render();

        // Edit own document
        ShareUser.openDocumentDetailPage(drone, fileName).render();
        DocumentDetailsPage documentDetailsPage = ShareUser.editTextDocument(drone, fileNameEdited, fileNameEdited, fileNameEdited).render();

        // Edit own comment to the document
        documentDetailsPage.render();
        documentDetailsPage.editComment(testName, editedItem);
        documentDetailsPage.saveEditComments().render(maxWaitTime);
        assertTrue(documentDetailsPage.isCommentCorrect(editedItem), "Comment wasn't edited");

        // Edit own Folder
        ShareUser.openDocumentLibrary(drone).render();
        ShareUser.editProperties(drone, folderName, folderNameEdited, folderNameEdited, folderNameEdited).render();

        // Edit own comment to the folder
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderNameEdited).render();
        folderDetailsPage.editComment(testName, editedItem);
        folderDetailsPage.saveEditComments().render();
        assertTrue(folderDetailsPage.isCommentCorrect(editedItem), "Comment wasn't edited");

        // Navigate to Calendar
        CalendarPage calendarPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectCalendarPage().render();

        // Edit own event
        calendarPage.editEvent(testName, CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, MONTH_TAB, editedItem, editedItem, editedItem, null, null, null, null,
                null, false, null).render();
        assertTrue(calendarPage.isEventPresent(CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, editedItem), "The event wasn't edited");

        // Navigate to Wiki
        WikiPage wikiPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectWikiPage().render();
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn();

        // Edit own wiki page
        wikiPage = wikiPageList.editWikiPage(testName, editedItem);

        // Rename own wiki page
        wikiPage.renameWikiPage(editedItem);

        // Navigate to details page and Revert
        Double versionNum = 1.0;
        wikiPage.clickDetailsLink();
        wikiPage.revertToVersion(versionNum);
        assertEquals(new Double(1.3), wikiPage.getCurrentWikiVersion());

        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName);
        // Navigate to Discussions
        DiscussionsPage discussionsPage = dashboard.getSiteNav().selectDiscussionsPage().render();

        // Edit own topic
        TopicViewPage topicViewPage = discussionsPage.editTopic(testName, editedItem, editedItem).render();

        // Edit own reply
        topicViewPage.editReply(testName, editedItem);

        // Navigate to Blog
        BlogPage blogPage = dashBoard.getSiteNav().selectBlogPage().render();
        PostViewPage postViewPage = blogPage.openBlogPost(testName).render();
        postViewPage.editBlogPostAndUpdate(editedItem, editedItem, null);

        postViewPage.editBlogComment(testName, editedItem);

        // Navigate to Links
        LinksPage linksPage = dashBoard.getSiteNav().selectLinksPage().render();
        linksPage.editLink(testName, editedItem, editedItem, editedItem, true);

        // Navigate to Data Lists
        DataListPage dataListPage = (DataListPage) dashBoard.getSiteNav().selectDataListPage().render();

        // Edit own data list
        dataListPage.editDataList(testName, editedItem, editedItem);

        // Edit own item of the list
        dataListPage.selectDataList(editedItem);
        ContactList contactList = new ContactList(drone);
        contactList.editItem(testName, editedItem);

        // Navigate to Members
        SiteMembersPage siteMembersPage = dashBoard.getSiteNav().selectMembersPage().render();

        // Verify the possibility to edit user roles to the site
        siteMembersPage.assignRole(user3, UserRole.MANAGER);

        // Remove the changes
        siteMembersPage = dashBoard.getSiteNav().selectMembersPage();
        siteMembersPage.assignRole(user3, UserRole.COLLABORATOR);

        // Verify the possibility to edit group roles to the site
        ShareUserMembers.assignRoleToSiteGroup(drone, group1, siteName, UserRole.MANAGER);
    }

    /**
     * Check Edit own content for Collaborator
     */
    @Test(groups = "Sanity", dependsOnMethods = "AONE_15205")
    public void AONE_15209() throws Exception
    {
        /** Start Test */
        testName = getClass().getSimpleName() + uniqueRun;
        // String siteName = getSiteName(testName);

        /** Test Data Setup */
        String user2 = username + 2;
        String user3 = username + 3;
        String group1 = testName;
        testName += "collaborator";
        String editedItem = testName + "edited";
        String fileName = getFileName("collaborator");
        String folderName = getFolderName("collaborator");
        String fileNameEdited = fileName + "edited";
        String folderNameEdited = folderName + "edited";

        // User3 logs in
        ShareUser.login(drone, user3, DEFAULT_PASSWORD);

        // User3 opens Site Dashboard
        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName).render();

        // Navigate to Document Library
        ShareUser.openDocumentLibrary(drone).render();

        // Edit own document
        ShareUser.openDocumentDetailPage(drone, fileName).render();
        DocumentDetailsPage documentDetailsPage = ShareUser.editTextDocument(drone, fileNameEdited, fileNameEdited, fileNameEdited).render();

        // Edit own comment to the document
        documentDetailsPage.editComment(testName, editedItem);
        documentDetailsPage.saveEditComments().render(maxWaitTime);

        // Edit own Folder
        ShareUser.openDocumentLibrary(drone).render();
        ShareUser.editProperties(drone, folderName, folderNameEdited, folderNameEdited, folderNameEdited);

        // Edit own comment to the folder
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderNameEdited).render();
        folderDetailsPage.editComment(testName, editedItem);
        folderDetailsPage.saveEditComments();

        // Navigate to Calendar
        CalendarPage calendarPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectCalendarPage().render();

        // Edit own event
        calendarPage.editEvent(testName, CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, MONTH_TAB, editedItem, editedItem, editedItem, null, null, null, null,
                null, false, null);

        // Navigate to Wiki
        WikiPage wikiPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectWikiPage().render();
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn().render();

        // Edit own wiki page
        wikiPage = wikiPageList.editWikiPage(testName, editedItem);

        // Rename own wiki page
        wikiPage.renameWikiPage(editedItem);

        // Navigate to details page and Revert
        Double versionNum = 1.0;
        wikiPage.clickDetailsLink();
        wikiPage.revertToVersion(versionNum);
        assertEquals(new Double(1.3), wikiPage.getCurrentWikiVersion());
        ShareUser.openSiteDashboard(drone, siteName).render();

        // Navigate to Discussions
        DiscussionsPage discussionsPage = dashboard.getSiteNav().selectDiscussionsPage().render();

        // Edit own topic
        TopicViewPage topicViewPage = discussionsPage.editTopic(testName, editedItem, editedItem).render();

        // Edit own reply
        topicViewPage.editReply(testName, editedItem);

        // Navigate to Blog
        BlogPage blogPage = dashboard.getSiteNav().selectBlogPage().render();
        PostViewPage postViewPage = blogPage.openBlogPost(testName);
        postViewPage.editBlogPostAndUpdate(editedItem, editedItem, null).render();
        postViewPage.editBlogComment(testName, editedItem);

        // Navigate to Links
        LinksPage linksPage = dashboard.getSiteNav().selectLinksPage().render();
        linksPage.editLink(testName, editedItem, editedItem, editedItem, true);

        // Navigate to Data Lists
        DataListPage dataListPage = (DataListPage) dashboard.getSiteNav().selectDataListPage();

        // Edit own data list
        dataListPage.editDataList(testName, editedItem, editedItem);

        // Edit own item of the list
        dataListPage.selectDataList(editedItem);
        ContactList contactList = new ContactList(drone);
        contactList.editItem(testName, editedItem);

        // Navigate to Members
        SiteMembersPage siteMembersPage = dashboard.getSiteNav().selectMembersPage();

        // Verify the possibility to edit user roles to the site
        assertFalse(siteMembersPage.isAssignRolePresent(user2), "Assign role button is displayed");

        // Verify the possibility to edit group roles to the site
        SiteGroupsPage siteGroupsPage = siteMembersPage.navigateToSiteGroups();
        assertFalse(siteGroupsPage.isAssignRolePresent(group1), "Assign role button is displayed");
    }

    /**
     * Check Edit own content for Contributor
     */
    @Test(groups = "Sanity", dependsOnMethods = "AONE_15206")
    public void AONE_15210() throws Exception
    {
        /** Start Test */
        testName = getClass().getSimpleName() + uniqueRun;
        // String siteName = getSiteName(testName);

        /** Test Data Setup */
        String user2 = username + 2;
        String user4 = username + 4;
        String group1 = testName;
        testName += "contributor";
        String editedItem = testName + "edited";
        String fileName = getFileName("contributor");
        String folderName = getFolderName("contributor");
        String fileNameEdited = fileName + "edited";
        String folderNameEdited = folderName + "edited";

        // User3 logs in
        ShareUser.login(drone, user4, DEFAULT_PASSWORD);

        // User3 opens Site Dashboard
        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName).render();

        // Navigate to Document Library
        ShareUser.openDocumentLibrary(drone);

        // Edit own document
        ShareUser.openDocumentDetailPage(drone, fileName).render();
        DocumentDetailsPage documentDetailsPage = ShareUser.editTextDocument(drone, fileNameEdited, fileNameEdited, fileNameEdited).render();

        // Edit own comment to the document
        documentDetailsPage.editComment(testName, editedItem);
        documentDetailsPage.saveEditComments().render(maxWaitTime);

        // Edit own Folder
        ShareUser.openDocumentLibrary(drone).render();
        ShareUser.editProperties(drone, folderName, folderNameEdited, folderNameEdited, folderNameEdited).render();

        // Edit own comment to the folder
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderNameEdited).render();
        folderDetailsPage.editComment(testName, editedItem);
        folderDetailsPage.saveEditComments();

        // Navigate to Calendar
        CalendarPage calendarPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectCalendarPage().render();

        // Edit own event
        calendarPage.editEvent(testName, CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, MONTH_TAB, editedItem, editedItem, editedItem, null, null, null, null,
                null, false, null);

        // Navigate to Wiki
        WikiPage wikiPage = ShareUser.openSiteDashboard(drone, siteName).getSiteNav().selectWikiPage().render();

        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn().render();

        // Edit own wiki page
        wikiPage = wikiPageList.editWikiPage(testName, editedItem).render();

        // Rename own wiki page
        wikiPage.renameWikiPage(editedItem);

        // Navigate to details page and Revert
        Double versionNum = 1.0;
        wikiPage.clickDetailsLink();
        wikiPage.revertToVersion(versionNum);
        assertEquals(new Double(1.3), wikiPage.getCurrentWikiVersion());
        ShareUser.openSiteDashboard(drone, siteName).render();

        // Navigate to Discussions
        DiscussionsPage discussionsPage = dashboard.getSiteNav().selectDiscussionsPage().render();

        // Edit own topic
        TopicViewPage topicViewPage = discussionsPage.editTopic(testName, editedItem, editedItem).render();

        // Edit own reply
        topicViewPage.editReply(testName, editedItem);

        // Navigate to Blog
        BlogPage blogPage = dashboard.getSiteNav().selectBlogPage().render();
        PostViewPage postViewPage = blogPage.openBlogPost(testName).render();
        postViewPage.editBlogPostAndUpdate(editedItem, editedItem, null);
        postViewPage.editBlogComment(testName, editedItem);

        // Navigate to Links
        LinksPage linksPage = dashboard.getSiteNav().selectLinksPage().render();
        linksPage.editLink(testName, editedItem, editedItem, editedItem, true);

        // Navigate to Data Lists
        dashboard.getSiteNav().selectDataListPage();

        // Edit own data list
        DataListPage dataListPage = new DataListPage(drone).render();
        dataListPage.editDataList(testName, editedItem, editedItem);

        // Edit own item of the list
        dataListPage.selectDataList(editedItem);
        ContactList contactList = new ContactList(drone);
        contactList.editItem(testName, editedItem);

        // Navigate to Members
        SiteMembersPage siteMembersPage = dashboard.getSiteNav().selectMembersPage().render();

        // Verify the possibility to edit user roles to the site
        assertFalse(siteMembersPage.isAssignRolePresent(user2), "Assign role button is displayed");

        // Verify the possibility to edit group roles to the site
        SiteGroupsPage siteGroupsPage = siteMembersPage.navigateToSiteGroups();
        assertFalse(siteGroupsPage.isAssignRolePresent(group1), "Assign role button is displayed");
    }

    /**
     * Check Edit others content for Manager
     */
    @Test(groups = "Sanity", dependsOnMethods = "AONE_15209")
    public void AONE_15220() throws Exception
    {
        /** Start Test */
        testName = getClass().getSimpleName() + uniqueRun;
        // String siteName = getSiteName(testName);

        /** Test Data Setup */
        String user2 = username + 2;
        testName += "collaboratoredited";
        String createdItem = "createdByManager";
        String editedItem = "editedByManager";
        String editedFolder = "folderEditedByManager";
        String fileName = getFileName("collaboratoredited");
        String folderName = getFolderName("collaboratoredited");

        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // User2 opens Site Dashboard
        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName).render();

        // Navigate to Document Library
        ShareUser.openDocumentLibrary(drone);

        // Edit collaborator document
        ShareUser.openDocumentDetailPage(drone, fileName);
        DocumentDetailsPage documentDetailsPage = ShareUser.editTextDocument(drone, editedItem, editedItem, editedItem).render();

        // Add a comment to the document
        documentDetailsPage.addComment(createdItem);

        // Edit own comment to the document
        documentDetailsPage.editComment(createdItem, createdItem + " and then " + editedItem);
        documentDetailsPage.saveEditComments().render(maxWaitTime);

        // Edit collaborator's comment to the document
        documentDetailsPage.editComment(testName, editedItem);
        documentDetailsPage.saveEditComments().render(maxWaitTime);

        // Verify the available for collaborator's document actions
        assertTrue(documentDetailsPage.isDocumentActionPresent(DOWNLOAD_DOCUMENT), "Download action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(EDIT_PROPERTIES), "Edit Properties action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(VIEW_IN_EXLPORER), "View in Browser isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(UPLOAD_DOCUMENT), "Upload New Version action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(DOCUMENT_INLINE_EDIT), "Inline Edit action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(EDIT_OFFLINE), "Edit Offline action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(COPY_TO), "Copy to action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(MOVE_TO), "Move to action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(DELETE_CONTENT), "Delete action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(START_WORKFLOW), "Start Workflow action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(MANAGE_PERMISSION_DOC), "Manage Permissions action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(MANAGE_ASPECTS), "Manage aspects action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(CHNAGE_TYPE), "Change type action isn't available");

        // Verify icons near document section
        assertTrue(documentDetailsPage.isEditPropertiesIconDisplayed(), "Edit properties icon isn't displayed");
        assertTrue(documentDetailsPage.isTagIconDisplayed(), "Edit tags icon isn't displayed");
        assertTrue(documentDetailsPage.isEditPermissionsIconDisplayed(), "Edit permissions icon isn't displayed");
        assertTrue(documentDetailsPage.isUploadNewVersionDisplayed(), "Upload new version icon isn't displayed");
        assertTrue(documentDetailsPage.isStartWorkflowIconDisplayed(), "Start Workflow icon isn't displayed");

        // Verify the following links are displayed: Favourite, Like, Comment, QuickShare
        assertTrue(documentDetailsPage.isSharePanePresent());
        assertTrue(documentDetailsPage.isFavouriteLinkPresent());
        assertTrue(documentDetailsPage.isLikeLinkPresent());
        assertTrue(documentDetailsPage.isCommentLinkPresent());
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        assertTrue(documentLibraryPage.getFileDirectoryInfo(editedItem).isShareLinkVisible());

        // Edit collaborator's Folder
        ShareUser.editProperties(drone, folderName, editedFolder, editedFolder, editedFolder);

        // Add a comment to the folder
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, editedFolder).render();
        folderDetailsPage.addComment(createdItem);

        // Edit own comment to the folder
        folderDetailsPage.editComment(createdItem, createdItem + " and then " + editedItem);
        folderDetailsPage.saveEditComments();

        // Edit collaborator's comment to the folder
        folderDetailsPage.editComment(testName, editedItem);
        folderDetailsPage.saveEditComments();

        // Add some document to collaborator's folder
        ShareUser.openDocumentLibrary(drone);
        documentLibraryPage.getFileDirectoryInfo(editedFolder).clickOnTitle();
        ContentDetails contentDetails = new ContentDetails(createdItem);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, documentLibraryPage).render(maxWaitTime);

        // Verify the available for collaborator's folder actions
        ShareUser.openDocumentLibrary(drone).render();
        ShareUser.openFolderDetailPage(drone, editedFolder).render();

        // Verify the available folder actions
        assertTrue(folderDetailsPage.isDocumentActionPresent(DOWNLOAD_FOLDER));
        assertTrue(folderDetailsPage.isDocumentActionPresent(EDIT_PROPERTIES));
        assertTrue(folderDetailsPage.isDocumentActionPresent(COPY_TO));
        assertTrue(folderDetailsPage.isDocumentActionPresent(MOVE_TO));
        assertTrue(folderDetailsPage.isDocumentActionPresent(MANAGE_RULES));
        assertTrue(folderDetailsPage.isDocumentActionPresent(DELETE_CONTENT));
        assertTrue(folderDetailsPage.isDocumentActionPresent(MANAGE_PERMISSION_FOL));
        assertTrue(folderDetailsPage.isDocumentActionPresent(MANAGE_ASPECTS));
        assertTrue(folderDetailsPage.isDocumentActionPresent(CHNAGE_TYPE));

        // Verify the panes are available
        assertTrue(folderDetailsPage.isSharePanePresent(), "Share pane isn't displayed for the folder");
        assertTrue(folderDetailsPage.isTagPanelPresent(), "Tags pane isn't displayed for the folder");
        assertTrue(folderDetailsPage.isPropertiesPanelPresent(), "Properties pane isn't displayed for the folder");
        assertTrue(folderDetailsPage.isPermissionsPanelPresent(), "Permissions pane isn't displayed for the folder");

        // Verify Edit controls are displayed near the panes
        assertTrue(folderDetailsPage.isTagIconDisplayed(), "Tag icon isn't displayed");
        assertTrue(folderDetailsPage.isEditPropertiesIconDisplayed(), "Edit properties icon isn't displayed");
        assertTrue(folderDetailsPage.isEditPermissionsIconDisplayed(), "Edit permissions icon isn't displayed");

        // Verify the following actions are present: Favourite, Like, Comment
        assertTrue(folderDetailsPage.isFavouriteLinkPresent(), "Favourite link isn't displayed for the folder");
        assertTrue(folderDetailsPage.isLikeLinkPresent(), "Like link isn't displayed for the folder");
        assertTrue(folderDetailsPage.isCommentLinkPresent(), "Comment link isn't displayed for the folder");

        // Navigate to Calendar
        CalendarPage calendarPage = dashboard.getSiteNav().selectCalendarPage().render();

        // Edit collaborator's event
        calendarPage.editEvent(testName, CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, MONTH_TAB, editedItem, editedItem, editedItem, null, null, null, null,
                null, false, null);

        // Navigate to Wiki
        WikiPage wikiPage = dashboard.getSiteNav().selectWikiPage().render();

        // Edit collaborator's wiki page
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn().render();
        wikiPageList.editWikiPage(testName, editedItem);

        // Rename collaborator's wiki page
        wikiPage.renameWikiPage(editedItem);

        // Navigate to details of collaborator's wiki page and Revert
        Double versionNum = 1.0;
        wikiPage.clickDetailsLink();
        Double expVersion = wikiPage.getCurrentWikiVersion() + 0.1;
        wikiPage.revertToVersion(versionNum);
        assertEquals(wikiPage.getCurrentWikiVersion(), expVersion);

        // Navigate to Dicsussions
        DiscussionsPage discussionsPage = dashboard.getSiteNav().selectDiscussionsPage().render();

        // Edit collaborator's topic
        TopicViewPage topicViewPage = discussionsPage.editTopic(testName, editedItem, editedItem).render();

        // Leave a reply for the topic
        topicViewPage.createReply(createdItem);

        // Edit own reply for the topic
        topicViewPage.editReply(createdItem, createdItem + " and then " + editedItem);

        // Edit collaborator's reply for the topic
        topicViewPage.editReply(testName, createdItem);

        // Navigate to Blog
        BlogPage blogPage = dashboard.getSiteNav().selectBlogPage().render();

        // Edit collaborator's blog post
        PostViewPage postViewPage = blogPage.openBlogPost(testName).render();
        postViewPage.editBlogPostAndUpdate(editedItem, editedItem, null);

        // Add a comment to the post
        postViewPage.createBlogComment(createdItem).render();

        // Edit own comment to the post
        postViewPage.editBlogComment(createdItem, createdItem + " and then " + editedItem);

        // Edit collaborator's comment to the post
        postViewPage.editBlogComment(testName, editedItem);

        // Navigate to Links
        LinksPage linksPage = dashboard.getSiteNav().selectLinksPage().render();

        // Edit collaborator's link
        String url = "http://alfresco.com";
        linksPage.editLink(testName, editedItem, editedItem, url, true);

        // Navigate to Data Lists
        DataListPage dataListPage = (DataListPage) dashboard.getSiteNav().selectDataListPage();

        // Edit collaborator's data list
        dataListPage.editDataList(testName, editedItem, editedItem);

        // Add/duplicate any item to the list
        dataListPage.selectDataList(editedItem);
        ContactList contactList = new ContactList(drone);
        contactList.createItem(createdItem);
        contactList.duplicateAnItem(createdItem);

        // Edit own item of the list
        // ContactListItem contactListItem = new ContactListItem(drone);
        contactList.editItem(createdItem, createdItem + " and then " + editedItem);

        // Edit collaborator's item of the list
        contactList.editItem(testName, editedItem);
    }

    /**
     * Check Edit others content for Collaborator
     */
    @Test(groups = "Sanity", dependsOnMethods = "AONE_15210")
    public void AONE_15221() throws Exception
    {
        /** Start Test */
        testName = getClass().getSimpleName() + uniqueRun;
        // String siteName = getSiteName(testName);

        /** Test Data Setup */
        String user3 = username + 3;
        testName += "contributoredited";
        String createdItem = "createdByCollaborator";
        String editedItem = "editedByCollaborator";
        String editedFolder = "folderEditedByCollaborator";
        String fileName = getFileName("contributoredited");
        String folderName = getFolderName("contributoredited");

        ShareUser.login(drone, user3, DEFAULT_PASSWORD);

        // User2 opens Site Dashboard
        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName).render();

        // Navigate to Document Library
        ShareUser.openDocumentLibrary(drone).render();

        // Edit collaborator document
        ShareUser.openDocumentDetailPage(drone, fileName).render();
        DocumentDetailsPage documentDetailsPage = ShareUser.editTextDocument(drone, editedItem, editedItem, editedItem).render();

        // Add a comment to the document
        documentDetailsPage.addComment(createdItem);

        // Edit own comment to the document
        documentDetailsPage.editComment(createdItem, createdItem + " and then " + editedItem);
        documentDetailsPage.saveEditComments().render(maxWaitTime);

        // Verify it's not possible to edit contributor's comment to the document
        assertFalse(documentDetailsPage.isEditCommentButtonPresent(testName), "Edit comment action is present");

        // Verify the available for collaborator's document actions
        assertTrue(documentDetailsPage.isDocumentActionPresent(DOWNLOAD_DOCUMENT), "Download action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(EDIT_PROPERTIES), "Edit Properties action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(VIEW_IN_EXLPORER), "View in Browser isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(UPLOAD_DOCUMENT), "Upload New Version action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(DOCUMENT_INLINE_EDIT), "Inline Edit action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(EDIT_OFFLINE), "Edit Offline action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(COPY_TO), "Copy to action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(START_WORKFLOW), "Start Workflow action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(MANAGE_ASPECTS), "Manage aspects action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(CHNAGE_TYPE), "Change type action isn't available");

        // Verify icons near document section
        assertTrue(documentDetailsPage.isEditPropertiesIconDisplayed(), "Edit properties icon isn't displayed");
        assertTrue(documentDetailsPage.isTagIconDisplayed(), "Edit tags icon isn't displayed");
        assertTrue(documentDetailsPage.isUploadNewVersionDisplayed(), "Upload new version icon isn't displayed");
        assertTrue(documentDetailsPage.isStartWorkflowIconDisplayed(), "Start Workflow icon isn't displayed");

        // Verify the following links are displayed: Favourite, Like, Comment, QuickShare
        assertTrue(documentDetailsPage.isSharePanePresent());
        assertTrue(documentDetailsPage.isFavouriteLinkPresent());
        assertTrue(documentDetailsPage.isLikeLinkPresent());
        assertTrue(documentDetailsPage.isCommentLinkPresent());
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone);
        assertTrue(documentLibraryPage.getFileDirectoryInfo(editedItem).isShareLinkVisible());

        // Edit collaborator's Folder
        ShareUser.editProperties(drone, folderName, editedFolder, editedFolder, editedFolder).render();

        // Add a comment to the folder
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, editedFolder);
        folderDetailsPage.addComment(createdItem);

        // Edit own comment to the folder
        folderDetailsPage.editComment(createdItem, createdItem + " and then " + editedItem);
        folderDetailsPage.saveEditComments();

        // Verify it's not possible to edit contributor's comment to the folder
        assertFalse(folderDetailsPage.isEditCommentButtonPresent(testName), "Edit comment action is present");

        // Add some document to contributor's folder
        ShareUser.openDocumentLibrary(drone);
        documentLibraryPage.getFileDirectoryInfo(editedFolder).clickOnTitle();
        ContentDetails contentDetails = new ContentDetails(createdItem);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, documentLibraryPage).render(maxWaitTime);

        // Verify the available for contributor's folder actions
        ShareUser.openDocumentLibrary(drone);
        ShareUser.openFolderDetailPage(drone, editedFolder).render();

        // Verify the available folder actions
        assertTrue(folderDetailsPage.isDocumentActionPresent(DOWNLOAD_FOLDER));
        assertTrue(folderDetailsPage.isDocumentActionPresent(EDIT_PROPERTIES));
        assertTrue(folderDetailsPage.isDocumentActionPresent(COPY_TO));
        assertTrue(folderDetailsPage.isDocumentActionPresent(MANAGE_ASPECTS));
        assertTrue(folderDetailsPage.isDocumentActionPresent(CHNAGE_TYPE));

        // Verify the panes are available
        assertTrue(folderDetailsPage.isSharePanePresent(), "Share pane isn't displayed for the folder");
        assertTrue(folderDetailsPage.isTagPanelPresent(), "Tags pane isn't displayed for the folder");
        assertTrue(folderDetailsPage.isPropertiesPanelPresent(), "Properties pane isn't displayed for the folder");
        assertTrue(folderDetailsPage.isPermissionsPanelPresent(), "Permissions pane isn't displayed for the folder");

        // Verify Edit controls are displayed near the panes
        assertTrue(folderDetailsPage.isTagIconDisplayed(), "Tag icon isn't displayed");
        assertTrue(folderDetailsPage.isEditPropertiesIconDisplayed(), "Edit properties icon isn't displayed");

        // Verify the following actions are present: Favourite, Like, Comment
        assertTrue(folderDetailsPage.isFavouriteLinkPresent(), "Favourite link isn't displayed for the folder");
        assertTrue(folderDetailsPage.isLikeLinkPresent(), "Like link isn't displayed for the folder");
        assertTrue(folderDetailsPage.isCommentLinkPresent(), "Comment link isn't displayed for the folder");

        // Navigate to Calendar
        CalendarPage calendarPage = dashboard.getSiteNav().selectCalendarPage();

        // Edit collaborator's event
        calendarPage.editEvent(testName, CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, MONTH_TAB, editedItem, editedItem, editedItem, null, null, null, null,
                null, false, null);

        // Navigate to Wiki
        WikiPage wikiPage = dashboard.getSiteNav().selectWikiPage().render();

        // Edit collaborator's wiki page
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn().render();
        wikiPageList.editWikiPage(testName, editedItem);

        // Rename collaborator's wiki page
        wikiPage.renameWikiPage(editedItem);

        // Navigate to details of collaborator's wiki page and Revert
        Double versionNum = 1.0;
        wikiPage.clickDetailsLink();
        Double expVersion = wikiPage.getCurrentWikiVersion() + 0.1;
        wikiPage.revertToVersion(versionNum);
        assertEquals(wikiPage.getCurrentWikiVersion(), expVersion);

        // Navigate to Dicsussions
        DiscussionsPage discussionsPage = dashboard.getSiteNav().selectDiscussionsPage().render();

        // Verify it's not possible to edit contributor's topic
        assertFalse(discussionsPage.isEditTopicDisplayed(testName), "Edit topic is displayed");

        // Leave a reply for the topic
        TopicViewPage topicViewPage = discussionsPage.viewTopic(testName).render();
        topicViewPage.createReply(createdItem);

        // Edit own reply for the topic
        topicViewPage.editReply(createdItem, createdItem + " and then " + editedItem);

        // Edit contributor's reply for the topic
        assertFalse(topicViewPage.isEditReplyDisplayed(testName), "Edit reply is displayed");

        // Navigate to Blog
        BlogPage blogPage = dashboard.getSiteNav().selectBlogPage().render();

        // Edit contributor's blog post
        PostViewPage postViewPage = blogPage.openBlogPost(testName).render();
        postViewPage.editBlogPostAndUpdate(editedItem, editedItem, null);

        // Add a comment to the post
        postViewPage.createBlogComment(createdItem);

        // Edit own comment to the post
        postViewPage.editBlogComment(createdItem, createdItem + " and then " + editedItem);

        // Edit contributor's comment to the post
        assertFalse(postViewPage.isEditCommentDisplayed(testName), "Edit blog comment is displayed");

        // Navigate to Links
        LinksPage linksPage = dashboard.getSiteNav().selectLinksPage().render();

        // Edit collaborator's link
        String url = "http://alfresco.com";
        linksPage.editLink(testName, editedItem, editedItem, url, true);

        // Navigate to Data Lists
        DataListPage dataListPage = (DataListPage) dashboard.getSiteNav().selectDataListPage();

        // Edit collaborator's data list
        dataListPage.editDataList(testName, editedItem, editedItem);

        // Add/duplicate any item to the list
        dataListPage.selectDataList(editedItem);
        ContactList contactList = new ContactList(drone);
        contactList.createItem(createdItem);
        contactList.duplicateAnItem(createdItem);

        // Edit own item of the list
        contactList.editItem(createdItem, createdItem + " and then " + editedItem);

        // Edit collaborator's item of the list
        contactList.editItem(testName, editedItem);
    }

    /**
     * Check Edit others content for Contributor
     */
    @Test(groups = "Sanity", dependsOnMethods = "AONE_15208")
    public void AONE_15222() throws Exception
    {
        /** Start Test */
        testName = getClass().getSimpleName() + uniqueRun;
        // String siteName = getSiteName(testName);

        /** Test Data Setup */
        String user4 = username + 4;
        testName += "manageredited";
        String createdItem = "createdByContributor";
        String editedItem = "editedByContributor";
        String fileName = getFileName("manageredited");
        String folderName = getFolderName("manageredited");

        ShareUser.login(drone, user4, DEFAULT_PASSWORD);

        // User2 opens Site Dashboard
        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName).render();

        // Navigate to Document Library
        ShareUser.openDocumentLibrary(drone);

        // Verify it's not possible to edit manager's document
        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName).render();
        assertFalse(documentDetailsPage.isDocumentActionPresent(UPLOAD_DOCUMENT), "Upload New Version is present");
        assertFalse(documentDetailsPage.isInlineEditLinkDisplayed(), "Inline Edit link is present");
        assertFalse(documentDetailsPage.isDocumentActionPresent(EDIT_PROPERTIES), "Edit properties is present");

        // Add a comment to the document
        documentDetailsPage.addComment(createdItem);

        // Edit own comment to the document
        documentDetailsPage.editComment(createdItem, createdItem + " and then " + editedItem);

        // Verify it's not possible to edit manager's comment to the document
        assertFalse(documentDetailsPage.isEditCommentButtonPresent(testName), "Edit comment action is present");

        // Verify the available for manager's document actions
        assertTrue(documentDetailsPage.isDocumentActionPresent(DOWNLOAD_DOCUMENT), "Download action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(VIEW_IN_EXLPORER), "View in Browser isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(START_WORKFLOW), "Start Workflow action isn't available");
        // assertTrue(documentDetailsPage.isDocumentActionPresent(PUBLISH_ACTION), "Publish action isn't available");

        // Verify icons near document section
        assertTrue(documentDetailsPage.isStartWorkflowIconDisplayed(), "Start Workflow icon isn't visible");

        // Verify Download button is available
        assertTrue(documentDetailsPage.isDownloadButtonVisible(), "Unable to find download button");

        // Verify the following links are displayed: Favourite, Like, Comment
        assertTrue(documentDetailsPage.isFavouriteLinkPresent());
        assertTrue(documentDetailsPage.isLikeLinkPresent());
        assertTrue(documentDetailsPage.isCommentLinkPresent());

        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Verify it's not possible to edit the folder
        assertFalse(documentLibraryPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent(), "Edit properties link is present");

        // Add a comment to the folder
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName).render();
        folderDetailsPage.addComment(createdItem);

        // Edit own comment to the folder
        folderDetailsPage.editComment(createdItem, createdItem + " and then " + editedItem);

        // Verify it's not possible to edit manager's comment to the folder
        assertFalse(folderDetailsPage.isEditCommentButtonPresent(testName), "Edit comment action is present");

        // Add some document to manager's folder
        ShareUser.openDocumentLibrary(drone);
        documentLibraryPage.getFileDirectoryInfo(folderName).clickOnTitle();
        ContentDetails contentDetails = new ContentDetails(createdItem);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, documentLibraryPage).render(maxWaitTime);

        // Verify the available for manager's folder actions
        folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName);
        assertTrue(folderDetailsPage.isDocumentActionPresent(DOWNLOAD_FOLDER), "Download as Zip option is not present");
        assertTrue(folderDetailsPage.isDocumentActionPresent(COPY_TO), "Copy to.. action isn't available");

        // There is no edit/manage icons near the folder sections
        assertTrue(folderDetailsPage.getEditControls().size() == 0, "There is edit/manage controls");

        // Verify the following actions are present: Favourite, Like
        assertTrue(folderDetailsPage.isFavouriteLinkPresent(), "Favourite link isn't displayed for the folder");
        assertTrue(folderDetailsPage.isLikeLinkPresent(), "Like link isn't displayed for the folder");
        assertTrue(folderDetailsPage.isCommentLinkPresent(), "Comment link is absent");

        // Navigate to Calendar
        CalendarPage calendarPage = dashboard.getSiteNav().selectCalendarPage().render();

        // Edit manager's event
        InformationEventForm informationEventForm = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, testName);
        assertFalse(informationEventForm.isEditButtonPresent(), "Edit button is present");
        informationEventForm.clickClose();

        // Navigate to Wiki
        WikiPage wikiPage = dashboard.getSiteNav().selectWikiPage().render();

        // Verify it's not possible to edit manager's wiki
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn();
        assertFalse(wikiPageList.getWikiPageDirectoryInfo(testName).isEditLinkPresent(), "Edit link is available");

        // Verify rename button is disabled
        wikiPage = wikiPageList.getWikiPageDirectoryInfo(testName).clickDetails();
        assertFalse(wikiPage.isRenameEnabled(), "Rename button is enabled");

        // Verify Revert button is disabled
        assertFalse(wikiPage.isRevertEnabled(), "Revert button is visible");

        // Navigate to Dicsussions
        DiscussionsPage discussionsPage = dashboard.getSiteNav().selectDiscussionsPage().render();

        // Verify it's not possible to edit the topic
        assertFalse(discussionsPage.getTopicDirectoryInfo(testName).isEditTopicDisplayed(), "Edit topic is available");

        // Leave a reply for the topic
        TopicViewPage topicViewPage = discussionsPage.getTopicDirectoryInfo(testName).viewTopic().render();
        topicViewPage.createReply(createdItem);

        // Edit own reply for the topic
        topicViewPage.editReply(createdItem, createdItem + " and then " + editedItem);

        // Edit manager's reply for the topic
        assertFalse(topicViewPage.isEditReplyDisplayed(testName), "Edit Reply link is available");

        // Navigate to Blog
        BlogPage blogPage = dashboard.getSiteNav().selectBlogPage().render();
        PostViewPage postViewPage = blogPage.openBlogPost(testName).render();
        assertFalse(postViewPage.isEditPostDisplayed(), "Edit Post is available");

        // Add a comment to the post
        postViewPage.createBlogComment(createdItem);

        // Edit own comment to the post
        postViewPage.editBlogComment(createdItem, createdItem + " and then " + editedItem);

        // Edit manager's comment to the post
        assertFalse(postViewPage.isEditCommentDisplayed(testName), "Edit comment is displayed");

        // Navigate to Links
        LinksPage linksPage = dashboard.getSiteNav().selectLinksPage();

        // Verify it's not possible to edit the link
        assertFalse(linksPage.isEditLinkDisplayed(testName), "Edit link is available!");

        // Navigate to Data Lists
        DataListPage dataListPage = (DataListPage) dashboard.getSiteNav().selectDataListPage();

        // Verify it's not possible to edit manager's data list
        assertFalse(dataListPage.isEditDataListDisplayed(testName), "Edit is available");

        // Add/duplicate any item to the list
        dataListPage.selectDataList(testName);
        ContactList contactList = new ContactList(drone);
        contactList.createItem(createdItem);
        contactList.duplicateAnItem(createdItem);

        // Edit own item of the list
        contactList.editItem(createdItem, createdItem + " and then " + editedItem);

        // Verify it's not possible to edit manager's item of the list
        assertFalse(contactList.isEditDisplayed(testName), "Edit item is available");
    }

    /**
     * Edit. Consumer
     * 
     * @throws Exception
     */
    @Test(groups = "Sanity", dependsOnMethods = "AONE_15208")
    public void AONE_15223() throws Exception
    {
        /** Start Test */
        testName = getClass().getSimpleName() + uniqueRun;
        // String siteName = getSiteName(testName);
        /** Test Data Setup */
        String user5 = username + 5;
        testName += "manageredited";
        String fileName = getFileName("manageredited");
        String folderName = getFolderName("manageredited");

        ShareUser.login(drone, user5, DEFAULT_PASSWORD);

        // User5 opens Site Dashboard
        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName).render();

        // Navigate to Document Library
        ShareUser.openDocumentLibrary(drone);

        // Verify it's not possible to edit documents
        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName);
        assertFalse(documentDetailsPage.isDocumentActionPresent(UPLOAD_DOCUMENT), "Upload New Version is present");
        assertFalse(documentDetailsPage.isInlineEditLinkDisplayed(), "Inline Edit link is present");
        assertFalse(documentDetailsPage.isDocumentActionPresent(EDIT_PROPERTIES), "Edit properties is present");

        // Verify it's not possible to edit comment to any document
        assertFalse(documentDetailsPage.isEditCommentButtonPresent(testName), "Edit comment button is available");

        // Verify the available for manager's document actions
        assertTrue(documentDetailsPage.isDocumentActionPresent(DOWNLOAD_DOCUMENT), "Download action isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(VIEW_IN_EXLPORER), "View in Browser isn't available");
        assertTrue(documentDetailsPage.isDocumentActionPresent(START_WORKFLOW), "Start Workflow action isn't available");
        // assertTrue(documentDetailsPage.isDocumentActionPresent(PUBLISH_ACTION), "Publish action isn't available");

        // Verify icons near document section
        assertTrue(documentDetailsPage.isStartWorkflowIconDisplayed(), "Start Workflow icon isn't visible");

        // Verify Download button is available
        assertTrue(documentDetailsPage.isDownloadButtonVisible(), "Unable to find download button");

        // Verify the following links are displayed: Favourite, Like
        assertTrue(documentDetailsPage.isFavouriteLinkPresent());
        assertTrue(documentDetailsPage.isLikeLinkPresent());

        // Verify it's not possible to edit a folder
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        assertFalse(documentLibraryPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent(), "Edit properties link is present");

        // Verify it's not possible to edit a comment to any folder
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName).render();
        assertFalse(folderDetailsPage.isEditCommentButtonPresent(testName));

        // Verify the available folder actions for any folders
        assertTrue(folderDetailsPage.isDocumentActionPresent(DOWNLOAD_FOLDER), "Download as Zip option is not present");
        assertTrue(folderDetailsPage.isDocumentActionPresent(COPY_TO), "Copy to.. action isn't available");

        // There is no edit/manage icons near the folder sections
        assertTrue(folderDetailsPage.getEditControls().size() == 0, "There is edit/manage controls");

        // Verify the following actions are present: Favourite, Like
        assertTrue(folderDetailsPage.isFavouriteLinkPresent(), "Favourite link isn't displayed for the folder");
        assertTrue(folderDetailsPage.isLikeLinkPresent(), "Like link isn't displayed for the folder");

        // Navigate to Calendar
        CalendarPage calendarPage = dashboard.getSiteNav().selectCalendarPage().render();

        // Verify it's not possible to edit an event (check on all the tabs)
        calendarPage.chooseDayTab().render();
        InformationEventForm informationEventForm = calendarPage.clickOnEvent(CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, testName).render();
        assertFalse(informationEventForm.isEditButtonPresent(), "Edit button is available");
        informationEventForm.clickClose();

        calendarPage.chooseWeekTab().render();
        informationEventForm = calendarPage.clickOnEvent(CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, testName).render();
        assertFalse(informationEventForm.isEditButtonPresent(), "Edit button is available");
        informationEventForm.clickClose();

        calendarPage.chooseMonthTab().render();
        informationEventForm = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, testName).render();
        assertFalse(informationEventForm.isEditButtonPresent(), "Edit button is available");
        informationEventForm.clickClose();

        calendarPage.chooseAgendaTab().render();
        informationEventForm = calendarPage.clickOnEvent(CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, testName).render();
        assertFalse(informationEventForm.isEditButtonPresent(), "Edit button is available");
        informationEventForm.clickClose();

        // Navigate to Wiki
        WikiPage wikiPage = dashboard.getSiteNav().selectWikiPage().render();

        // Edit/rename/revert any wiki page
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn().render();
        assertFalse(wikiPageList.getWikiPageDirectoryInfo(testName).isEditLinkPresent(), "Edit link is present");

        wikiPage = wikiPageList.getWikiPageDirectoryInfo(testName).clickDetails().render();
        assertFalse(wikiPage.isRenameEnabled(), "Rename is displayed");
        assertFalse(wikiPage.isRevertEnabled(), "Revert is displayed");

        // Navigate to Dicsussions
        DiscussionsPage discussionsPage = dashboard.getSiteNav().selectDiscussionsPage().render();

        // Verify it's not possible to Edit any topic
        assertFalse(discussionsPage.isEditTopicDisplayed(testName), "Edit topic button is present");

        // Verify it's not possible to edit a reply
        TopicViewPage topicViewPage = discussionsPage.viewTopic(testName).render();
        assertFalse(topicViewPage.isEditReplyDisplayed(testName), "Edit reply link is present");

        // Navigate to Blog
        BlogPage blogPage = dashboard.getSiteNav().selectBlogPage().render();

        // Verify it's not possible to edit a post
        PostViewPage postViewPage = blogPage.openBlogPost(testName).render();
        assertFalse(postViewPage.isEditPostDisplayed(), "Edit post is available");

        // Verify it's not possible to edit a comment
        assertFalse(postViewPage.isEditCommentDisplayed(testName), "Edit comment link is displayed");

        // Navigate to Links
        LinksPage linksPage = dashboard.getSiteNav().selectLinksPage().render();

        // Verify it's not possible to edit a link
        assertFalse(linksPage.isEditLinkDisplayed(testName), "Edit link is displayed");

        // Navigate to Data Lists
        DataListPage dataListPage = dashboard.getSiteNav().selectDataListPage().render();

        // Verify it's not possible to edit a data list
        assertFalse(dataListPage.isEditDataListDisplayed(testName), "Edit data list is available");

        // Verify it's not possible to edit any item in any list
        dataListPage.selectDataList(testName);
        ContactList contactList = new ContactList(drone).render();
        assertFalse(contactList.isEditDisplayed(testName), "Edit item is available");

        // Navigate to Members
        SiteMembersPage siteMembersPage = dashboard.getSiteNav().selectMembersPage().render();

        // Verify it is not possible to invite users and groups
        assertFalse(siteMembersPage.isInviteLinkPresent(), "Invite link is displayed");
        SiteGroupsPage siteGroupsPage = siteMembersPage.navigateToSiteGroups();
        assertFalse(siteGroupsPage.isAddGroupDisplayed(), "Add groups is available");
    }

    /**
     * Delete. Consumer
     * 
     * @throws Exception
     */
    @Test(groups = "Sanity", dependsOnMethods = "AONE_15208")
    public void AONE_15224() throws Exception
    {
        /** Start Test */
        testName = getClass().getSimpleName() + uniqueRun;
        // String siteName = getSiteName(testName);

        /** Test Data Setup */
        String user5 = username + 5;
        testName += "manageredited";
        String fileName = getFileName("manageredited");
        String folderName = getFolderName("manageredited");

        ShareUser.login(drone, user5, DEFAULT_PASSWORD);

        // User5 opens Site Dashboard
        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName).render();

        // Navigate to Document Library
        ShareUser.openDocumentLibrary(drone).render();

        // Verify it's not possible to delete any document
        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName).render();
        assertFalse(documentDetailsPage.isDocumentActionPresent(DELETE_CONTENT), "Delete action is present");

        // Verify it's not possible to delete comment to any document
        assertFalse(documentDetailsPage.isDeleteCommentButtonPresent(testName), "Delete comment button is available");

        // Verify it's not possible to delete a folder
        ShareUser.openDocumentLibrary(drone).render();
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName).render();
        assertFalse(folderDetailsPage.isDocumentActionPresent(DELETE_CONTENT), "Delete folder link is present");

        // Verify it's not possible to delete a comment to any folder
        assertFalse(folderDetailsPage.isDeleteCommentButtonPresent(testName));

        // Navigate to Calendar
        CalendarPage calendarPage = dashboard.getSiteNav().selectCalendarPage().render();

        // Verify it's not possible to delete an event (check on all the tabs)
        calendarPage.chooseDayTab().render();
        InformationEventForm informationEventForm = calendarPage.clickOnEvent(CalendarPage.EventType.DAY_TAB_ALL_DAY_EVENT, testName).render();
        assertFalse(informationEventForm.isDeleteButtonPresent(), "Delete button is available");
        informationEventForm.clickClose();

        calendarPage.chooseWeekTab().render();
        informationEventForm = calendarPage.clickOnEvent(CalendarPage.EventType.WEEK_TAB_ALL_DAY_EVENT, testName).render();
        assertFalse(informationEventForm.isDeleteButtonPresent(), "Delete button is available");
        informationEventForm.clickClose();

        calendarPage.chooseMonthTab().render();
        informationEventForm = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, testName).render();
        assertFalse(informationEventForm.isDeleteButtonPresent(), "Delete button is available");
        informationEventForm.clickClose();

        calendarPage.chooseAgendaTab().render();
        informationEventForm = calendarPage.clickOnEvent(CalendarPage.EventType.AGENDA_TAB_ALL_DAY_EVENT, testName).render();
        assertFalse(informationEventForm.isDeleteButtonPresent(), "Delete button is available");
        informationEventForm.clickClose();

        // Navigate to Wiki
        WikiPage wikiPage = dashboard.getSiteNav().selectWikiPage().render();

        // Delete/rename/revert any wiki page
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn().render();
        assertFalse(wikiPageList.getWikiPageDirectoryInfo(testName).isDeleteLinkPresent(), "Delete link is present");

        wikiPage = wikiPageList.getWikiPageDirectoryInfo(testName).clickDetails().render();
        assertFalse(wikiPage.isRenameEnabled(), "Rename is displayed");
        assertFalse(wikiPage.isRevertEnabled(), "Revert is displayed");

        // Navigate to Dicsussions
        DiscussionsPage discussionsPage = dashboard.getSiteNav().selectDiscussionsPage().render();

        // Verify it's not possible to Delete any topic
        assertFalse(discussionsPage.isDeleteTopicDisplayed(testName), "Delete topic button is present");

        // Verify it's not possible to edit a reply
        TopicViewPage topicViewPage = discussionsPage.viewTopic(testName).render();
        assertFalse(topicViewPage.isDeleteReplyDisplayed(testName), "Delete reply link is present");

        // Navigate to Blog
        BlogPage blogPage = dashboard.getSiteNav().selectBlogPage().render();

        // Verify it's not possible to delete a post
        PostViewPage postViewPage = blogPage.openBlogPost(testName).render();
        assertFalse(postViewPage.isDeletePostDisplayed(), "Delete post is available");

        // Verify it's not possible to delete a comment
        assertFalse(postViewPage.isDeleteCommentDisplayed(testName), "Delete comment link is displayed");

        // Navigate to Links
        LinksPage linksPage = dashboard.getSiteNav().selectLinksPage().render();

        // Verify it's not possible to edit a link
        assertFalse(linksPage.isDeleteLinkDisplayed(testName), "Delete link is displayed");

        // Navigate to Data Lists
        DataListPage dataListPage = dashboard.getSiteNav().selectDataListPage().render();

        // Verify it's not possible to delete a data list
        assertFalse(dataListPage.isDeleteDataListDisplayed(testName), "Delete data list is available");

        // Verify it's not possible to delete any item in any list
        dataListPage.selectDataList(testName);
        ContactList contactList = new ContactList(drone).render();
        assertFalse(contactList.isDeleteDisplayed(testName), "Delete item is available");

        // Navigate to Members
        SiteMembersPage siteMembersPage = dashboard.getSiteNav().selectMembersPage().render();

        // Verify it is not possible to invite users and groups
        assertFalse(siteMembersPage.isInviteLinkPresent(), "Invite link is displayed");

        SiteGroupsPage siteGroupsPage = siteMembersPage.navigateToSiteGroups();
        assertFalse(siteGroupsPage.isAddGroupDisplayed(), "Add groups is available");
    }

    /**
     * Delete others. Contributor
     * 
     * @throws Exception
     */
    @Test(groups = "Sanity", dependsOnMethods = "AONE_15220")
    public void AONE_15225() throws Exception
    {
        /** Start Test */
        testName = getClass().getSimpleName() + uniqueRun;
        // String siteName = getSiteName(testName);

        /** Test Data Setup */
        String group1 = testName + uniqueRun;
        String user4 = username + 4;
        testName = "editedByManager";
        String text = "createdByContributor";
        String fileName = "editedByManager";
        String folderName = "folderEditedByManager";

        ShareUser.login(drone, user4, DEFAULT_PASSWORD);

        // User5 opens Site Dashboard
        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName).render();

        // Navigate to Document Library
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Verify it's not possible to delete collaborator's document
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isDeletePresent(), "Delete action is present from library page");
        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName).render();
        assertFalse(documentDetailsPage.isDocumentActionPresent(DELETE_CONTENT), "Delete action is present from details page");

        // Delete own comment to the document
        documentDetailsPage.addComment(text);
        documentDetailsPage = documentDetailsPage.deleteComment(text).render(7000);
        assertFalse(documentDetailsPage.getComments().contains(text), "The comment wasn't deleted");

        // Verify it's not possible to delete collaborator's comment
        assertFalse(documentDetailsPage.isDeleteCommentButtonPresent(testName), "Delete link is present for collaborator's comment");

        // Verify it's not possible to delete collaborator's folder
        documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        assertFalse(documentLibraryPage.getFileDirectoryInfo(folderName).isDeletePresent(), "Delete action is present from library page");
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName).render();
        assertFalse(folderDetailsPage.isDocumentActionPresent(DELETE_CONTENT), "Delete action is present from details page");

        // Delete own comment to the folder
        folderDetailsPage.addComment(text);
        folderDetailsPage.deleteComment(text);
        folderDetailsPage.render();
        waitInSeconds(3);
        assertFalse(folderDetailsPage.getComments().contains(text), "The comment wasn't deleted");

        // Verify it's not possible to delete collaborator's comment
        assertFalse(folderDetailsPage.isDeleteCommentButtonPresent(testName), "Delete link is present for collaborator's comment");

        // Delete own document from collaborator's folder
        ShareUser.openDocumentLibrary(drone);
        documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(folderName).clickOnTitle().render();
        ShareUser.refreshSharePage(drone);
        documentLibraryPage.render();
        ContentDetails contentDetails = new ContentDetails(text);
        ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, documentLibraryPage).render(maxWaitTime);
        documentLibraryPage.getFileDirectoryInfo(folderName).clickOnTitle();
        documentDetailsPage = ShareUser.openDocumentDetailPage(drone, text).render();
        documentLibraryPage = documentDetailsPage.delete().render();
        assertFalse(documentLibraryPage.isItemVisble(text), "Item wasn't deleted");

        // Navigate to Calendar
        CalendarPage calendarPage = dashboard.getSiteNav().selectCalendarPage().render();

        // Verify it's not possible to delete collaborator's event
        InformationEventForm informationEventForm = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, testName);
        assertFalse(informationEventForm.isDeleteButtonPresent(), "Delete button is available");
        informationEventForm.clickClose();

        // Navigate to Wiki
        WikiPage wikiPage = dashboard.getSiteNav().selectWikiPage().render();

        // Verify it's not possible to delete collaborator's wiki
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn().render();
        assertFalse(wikiPageList.getWikiPageDirectoryInfo(testName).isDeleteLinkPresent(), "Delete link for collaborator's wiki is present");

        // Navigate to Dicsussions
        DiscussionsPage discussionsPage = dashboard.getSiteNav().selectDiscussionsPage().render();

        // Verify it's not possible to delete collaborator's topic
        assertFalse(discussionsPage.isDeleteTopicDisplayed(testName), "Delete topic is displayed");

        // Delete own/collaborator reply for the topic
        // Not possible to delete replies until MNT-11398 is implemented

        // Navigate to Blog
        BlogPage blogPage = dashboard.getSiteNav().selectBlogPage().render();

        // Verify it's not possible to delete collaborator's post
        PostViewPage postViewPage = blogPage.openBlogPost(testName);
        assertFalse(postViewPage.isDeletePostDisplayed(), "Delete post is available");

        // Delete own comment to the post
        int expCount = postViewPage.getCommentCount();
        postViewPage.createBlogComment(text).render();
        postViewPage.deleteCommentWithConfirm(text).render();
        assertEquals(postViewPage.getCommentCount(), expCount, "Comment wasn't deleted");

        // Verify it's not possible to delete collaborator's comment
        assertFalse(postViewPage.isDeleteCommentDisplayed(testName), "Delete comment is available");

        // Navigate to Links
        LinksPage linksPage = dashboard.getSiteNav().selectLinksPage().render();

        // Verify it's not possible to delete collaborator's link
        assertFalse(linksPage.isDeleteLinkDisplayed(testName), "Delete option is available");

        // Navigate to Data Lists
        DataListPage dataListPage = dashboard.getSiteNav().selectDataListPage().render();

        // Verify it's not possible to delete the data list
        assertFalse(dataListPage.isDeleteDataListDisplayed(testName), "Delete Data list is available");

        // Delete own item of the list
        dataListPage.selectDataList(testName);
        ContactList contactList = new ContactList(drone).render();
        int expNum = contactList.getItemsCount();
        contactList.createItem(text).deleteAnItemWithConfirm(text);
        assertEquals(contactList.getItemsCount(), expNum);

        // Verify it's not possible to delete collaborator's item
        assertFalse(contactList.isDeleteDisplayed(testName), "Delete option is available");

        // Navigate to Members
        SiteMembersPage siteMembersPage = dashboard.getSiteNav().selectMembersPage().render();

        // Only People and Groups tags are present
        assertTrue(siteMembersPage.isGroupLinkPresent(), "Group link is missing");
        assertTrue(siteMembersPage.isPeopleLinkPresent(), "People link is missing");
        assertFalse(siteMembersPage.isInviteLinkPresent(), "Invite link is available");

        // Verify it is not possible to remove users and groups
        assertFalse(siteMembersPage.isRemoveButtonPresent(user4));

        SiteGroupsPage siteGroupsPage = siteMembersPage.navigateToSiteGroups();
        assertFalse(siteGroupsPage.isRemoveButtonPresent(group1), "Remove button is available for " + group1);
    }

    /**
     * Delete others. Collaborator
     * 
     * @throws Exception
     */
    @Test(groups = "Sanity", dependsOnMethods = "AONE_15208")
    public void AONE_15226() throws Exception
    {
        /** Start Test */
        testName = getClass().getSimpleName() + uniqueRun;
        // String siteName = getSiteName(testName);

        /** Test Data Setup */
        String group1 = testName;
        String user3 = username + 3;
        testName += "manageredited";
        String text = "createdByCollaborator";
        String fileName = getFileName("manageredited");
        String folderName = getFolderName("manageredited");

        ShareUser.login(drone, user3, DEFAULT_PASSWORD);

        // User3 opens Site Dashboard
        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName).render();

        // Navigate to Document Library
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();

        // Verify it's not possible to delete manager's document
        assertFalse(documentLibraryPage.getFileDirectoryInfo(fileName).isDeletePresent(), "Delete action is present from library page");
        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName).render();
        assertFalse(documentDetailsPage.isDocumentActionPresent(DELETE_CONTENT), "Delete action is present from details page");

        // Delete own comment to the document
        documentDetailsPage.addComment(text);
        documentDetailsPage.deleteComment(text);
        assertFalse(documentDetailsPage.getComments().contains(text), "The comment wasn't deleted");

        // Verify it's not possible to delete manager's comment
        assertFalse(documentDetailsPage.isDeleteCommentButtonPresent(testName), "Delete link is present for manager's comment");

        // Verify it's not possible to delete manager's folder
        documentLibraryPage = ShareUser.openDocumentLibrary(drone);
        assertFalse(documentLibraryPage.getFileDirectoryInfo(folderName).isDeletePresent(), "Delete action is present from library page");
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName).render();
        assertFalse(folderDetailsPage.isDocumentActionPresent(DELETE_CONTENT), "Delete action is present from details page");

        // Delete own comment to the folder
        folderDetailsPage.addComment(text);
        folderDetailsPage = folderDetailsPage.deleteComment(text).render();
        waitInSeconds(2);
        assertFalse(folderDetailsPage.getComments().contains(text), "The comment wasn't deleted");

        // Verify it's not possible to delete manager's comment
        assertFalse(folderDetailsPage.isDeleteCommentButtonPresent(testName), "Delete link is present for manager's comment");

        // Delete own document from manager's folder
        ShareUser.openDocumentLibrary(drone);
        documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(folderName).clickOnTitle().render();
        ShareUser.refreshSharePage(drone);
        documentLibraryPage.render();
        ContentDetails contentDetails = new ContentDetails(text);
        documentLibraryPage = ShareUser.createContentInCurrentFolder(drone, contentDetails, ContentType.PLAINTEXT, documentLibraryPage).render(maxWaitTime);
        documentLibraryPage.getFileDirectoryInfo(folderName).clickOnTitle();
        documentDetailsPage = ShareUser.openDocumentDetailPage(drone, text).render();
        documentLibraryPage = documentDetailsPage.delete().render();
        assertFalse(documentLibraryPage.isItemVisble(text), "Item wasn't deleted");

        // Navigate to Calendar
        CalendarPage calendarPage = dashboard.getSiteNav().selectCalendarPage().render();

        // Verify it's not possible to delete manager's event
        InformationEventForm informationEventForm = calendarPage.clickOnEvent(CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, testName);
        assertFalse(informationEventForm.isDeleteButtonPresent(), "Delete button is available");
        informationEventForm.clickClose();

        // Navigate to Wiki
        WikiPage wikiPage = dashboard.getSiteNav().selectWikiPage().render();

        // Verify it's not possible to delete manager's wiki
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn().render();
        assertFalse(wikiPageList.getWikiPageDirectoryInfo(testName).isDeleteLinkPresent(), "Delete link for manager's wiki is present");

        // Navigate to Dicsussions
        DiscussionsPage discussionsPage = dashboard.getSiteNav().selectDiscussionsPage().render();

        // Verify it's not possible to delete manager's topic
        assertFalse(discussionsPage.isDeleteTopicDisplayed(testName), "Delete topic is displayed");

        // Delete own/collaborator reply for the topic
        // Not possible to delete replies until MNT-11398 is implemented

        // Navigate to Blog
        BlogPage blogPage = dashboard.getSiteNav().selectBlogPage().render();

        // Verify it's not possible to delete manager's post
        PostViewPage postViewPage = blogPage.openBlogPost(testName).render();
        assertFalse(postViewPage.isDeletePostDisplayed(), "Delete post is available");

        // Delete own comment to the post
        int expCount = postViewPage.getCommentCount();
        postViewPage.createBlogComment(text).render();
        postViewPage.deleteCommentWithConfirm(text).render();
        assertEquals(postViewPage.getCommentCount(), expCount, "Comment wasn't deleted");

        // Verify it's not possible to delete manager's comment
        assertFalse(postViewPage.isDeleteCommentDisplayed(testName), "Delete comment is available");

        // Navigate to Links
        LinksPage linksPage = dashboard.getSiteNav().selectLinksPage().render();

        // Verify it's not possible to delete manager's link
        assertFalse(linksPage.isDeleteLinkDisplayed(testName), "Delete option is available");

        // Navigate to Data Lists
        DataListPage dataListPage = dashboard.getSiteNav().selectDataListPage().render();

        // Verify it's not possible to delete the data list
        assertFalse(dataListPage.isDeleteDataListDisplayed(testName), "Delete Data list is available");

        // Delete own item of the list
        dataListPage.selectDataList(testName);
        ContactList contactList = new ContactList(drone).render();
        int expNum = contactList.getItemsCount();
        contactList.createItem(text).deleteAnItemWithConfirm(text);
        assertEquals(contactList.getItemsCount(), expNum);

        // Verify it's not possible to delete manager's item
        assertFalse(contactList.isDeleteDisplayed(testName), "Delete option is available");

        // Navigate to Members
        SiteMembersPage siteMembersPage = dashboard.getSiteNav().selectMembersPage().render();

        // Only People and Groups tags are present
        assertTrue(siteMembersPage.isGroupLinkPresent(), "Group link is missing");
        assertTrue(siteMembersPage.isPeopleLinkPresent(), "People link is missing");
        assertFalse(siteMembersPage.isInviteLinkPresent(), "Invite link is available");

        // Verify it is not possible to remove users and groups
        assertFalse(siteMembersPage.isRemoveButtonPresent(user3));

        SiteGroupsPage siteGroupsPage = siteMembersPage.navigateToSiteGroups();
        assertFalse(siteGroupsPage.isRemoveButtonPresent(group1), "Remove button is available for " + group1);
    }

    /**
     * Delete others. Manager
     * 
     * @throws Exception
     */
    @Test(groups = "Sanity", dependsOnMethods = { "AONE_15220", "AONE_15204" })
    public void AONE_15227() throws Exception
    {
        /** Start Test */
        testName = getClass().getSimpleName() + uniqueRun;
        // String siteName = getSiteName(testName);

        /** Test Data Setup */
        String group1 = testName;
        String user2 = username + 2;
        String user3 = username + 3;
        String ownItemCreated = "createdByManager";
        String ownItemEdited = "editedByManager";
        String collItem = "editedByManager";
        String folderName = "folderEditedByManager";

        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // User2 opens Site Dashboard
        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName).render();

        // Navigate to Document Library
        ShareUser.openDocumentLibrary(drone).render();

        // Delete own comment to the document
        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, collItem).render();
        documentDetailsPage.deleteComment(ownItemCreated + " and then " + ownItemEdited);

        // Delete collaborator's comment to the document
        documentDetailsPage.deleteComment(collItem).render();

        // Delete collaborator's document
        documentDetailsPage.delete().render();

        // Delete own comment to the folder
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName).render(maxWaitTime);
        ShareUser.refreshSharePage(drone);
        folderDetailsPage.render(6000);
        folderDetailsPage.deleteComment(ownItemCreated + " and then " + ownItemEdited);

        // Delete collaborator's comment to the folder
        folderDetailsPage.deleteComment(collItem);

        // Delete own document from collaborator's folder
        DocumentLibraryPage documentLibraryPage = ShareUser.openDocumentLibrary(drone).render();
        documentLibraryPage = documentLibraryPage.getFileDirectoryInfo(folderName).clickOnTitle().render();
        waitInSeconds(3);
        if (documentLibraryPage.isFileVisible(ownItemCreated))
        {
            ConfirmDeletePage confirmDeletePage = documentLibraryPage.getFileDirectoryInfo(ownItemCreated).selectDelete().render();
            confirmDeletePage.selectAction(ConfirmDeletePage.Action.Delete);
        }
        // Delete collaborator's Folder
        ShareUser.openDocumentLibrary(drone).deleteItem(folderName).render();

        // Navigate to Calendar
        CalendarPage calendarPage = dashboard.getSiteNav().selectCalendarPage().render();

        // Delete collaborator's event
        calendarPage.deleteEvent(collItem, CalendarPage.EventType.MONTH_TAB_ALL_DAY_EVENT, MONTH_TAB);

        // Navigate to Wiki
        WikiPage wikiPage = dashboard.getSiteNav().selectWikiPage().render();

        // Delete collaborator's wiki page
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn();
        wikiPageList.deleteWikiWithConfirm(collItem).render();

        // Navigate to Discussions
        DiscussionsPage discussionsPage = dashboard.getSiteNav().selectDiscussionsPage().render();

        // Delete own/collaborator's reply for the topic
        // Not possible to delete replies until MNT-11398 is implemented

        // Delete collaborator's topic
        int expCount = discussionsPage.getTopicsCount() - 1;
        discussionsPage.deleteTopicWithConfirm(collItem).render(6000);
        waitInSeconds(3);
        assertEquals(discussionsPage.getTopicsCount(), expCount);

        // Navigate to Blog
        BlogPage blogPage = dashboard.getSiteNav().selectBlogPage().render();

        // Delete own comment to the post
        PostViewPage postViewPage = blogPage.openBlogPost(collItem).render();
        postViewPage.deleteCommentWithConfirm(ownItemCreated + " and then " + ownItemEdited).render();

        // Delete collaborator's comment to the post
        postViewPage.deleteCommentWithConfirm(collItem).render();

        // Delete collaborator's blog post
        postViewPage.deleteBlogPostWithConfirm().render();

        // Navigate to Links
        LinksPage linksPage = dashboard.getSiteNav().selectLinksPage().render();

        // Delete collaborator's link
        assertTrue(linksPage.isDeleteLinkDisplayed(collItem));
        linksPage.deleteLinkWithConfirm(collItem).render();

        // Navigate to Data Lists
        DataListPage dataListPage = dashboard.getSiteNav().selectDataListPage().render();

        // Delete own item of the list
        dataListPage.selectDataList(collItem);
        ContactList contactList = new ContactList(drone).render();
        contactList.deleteAnItemWithConfirm(ownItemCreated + " and then " + ownItemEdited);

        // Delete collaborator's item of the list
        contactList.deleteAnItemWithConfirm(collItem);

        // Delete collaborator's data list
        dataListPage.deleteDataListWithConfirm(collItem).render();

        // Navigate to Members
        SiteMembersPage siteMembersPage = dashboard.getSiteNav().selectMembersPage().render();

        // Verify the possibility to remove user and group from the site
        siteMembersPage.removeUser(user3);
        SiteGroupsPage siteGroupsPage = siteMembersPage.navigateToSiteGroups().render();
        siteGroupsPage.removeGroup(group1).render();

        // Add the users/groups back
        ShareUserMembers.inviteUserToSiteWithRole(drone, user2, user3, siteName, UserRole.COLLABORATOR);
        ShareUserMembers.inviteGroupToSiteWithRole(drone, user2, group1, siteName, UserRole.COLLABORATOR);

        // Navigate to trashcan and restore all the items
        TrashCanPage trashCanPage = ShareUserProfile.navigateToTrashCan(drone).render();
        trashCanPage.selectAction(SelectActions.ALL).render();
        trashCanPage.selectedRecover().clickRecoverOK().render();

        if (trashCanPage.getTrashCanItems().size() > 0)
        {
            trashCanPage.selectAction(SelectActions.ALL).render();
            trashCanPage.selectedRecover().clickRecoverOK().render();
        }
    }

    /**
     * Delete own. Manager
     * 
     * @throws Exception
     */
    @Test(groups = "Sanity", dependsOnMethods = "AONE_15227")
    public void AONE_8263() throws Exception
    {
        /** Start Test */
        testName = getClass().getSimpleName() + uniqueRun;
        // String siteName = getSiteName(testName);

        /** Test Data Setup */
        String user1 = username + 1;
        String user2 = username + 2;
        testName += "manageredited";
        String fileName = getFileName("manageredited");
        String folderName = getFolderName("manageredited");

        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // User2 opens Site Dashboard
        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName).render();

        // Navigate to Document Library
        ShareUser.openDocumentLibrary(drone).render();

        // Delete own comment to the document
        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, fileName).render();
        documentDetailsPage.deleteComment(testName);

        // Delete own document
        documentDetailsPage.delete().render();

        // Delete own comment to the folder
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName);
        folderDetailsPage.deleteComment(testName);

        // Delete own Folder
        ShareUser.openDocumentLibrary(drone).deleteItem(folderName).render();

        // Navigate to Calendar
        CalendarPage calendarPage = dashboard.getSiteNav().selectCalendarPage().render();

        // Delete own event
        calendarPage.deleteEvent(testName, CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, MONTH_TAB).render();

        // Navigate to Wiki
        WikiPage wikiPage = dashboard.getSiteNav().selectWikiPage().render();

        // Delete own wiki page
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn().render();
        wikiPageList.deleteWikiWithConfirm(testName).render();

        // Navigate to Dicsussions
        DiscussionsPage discussionsPage = dashboard.getSiteNav().selectDiscussionsPage().render();

        // Delete own reply for the topic
        // Not possible to delete replies until MNT-11398 is implemented

        // Delete own topic
        int expCount = discussionsPage.getTopicsCount() - 1;
        discussionsPage.deleteTopicWithConfirm(testName).render(6000);
        waitInSeconds(3);
        assertEquals(discussionsPage.getTopicsCount(), expCount);

        // Navigate to Blog
        BlogPage blogPage = dashboard.getSiteNav().selectBlogPage().render();

        // Delete own comment to the post
        PostViewPage postViewPage = blogPage.openBlogPost(testName).deleteCommentWithConfirm(testName).render();

        // Delete own blog post
        postViewPage.deleteBlogPostWithConfirm().render();

        // Navigate to Links
        LinksPage linksPage = dashboard.getSiteNav().selectLinksPage().render();

        // Delete own link
        linksPage.deleteLinkWithConfirm(testName).render();

        // Navigate to Data Lists
        DataListPage dataListPage = dashboard.getSiteNav().selectDataListPage().render();

        // Delete own item of the list
        dataListPage.selectDataList(testName);
        ContactList contactList = new ContactList(drone).render();
        contactList.deleteAnItemWithConfirm(testName);

        // Delete own data list
        dataListPage.render();
        dataListPage.deleteDataListWithConfirm(testName).render();

        // Navigate to Members
        SiteMembersPage siteMembersPage = dashboard.getSiteNav().selectMembersPage().render();

        // Verify it is possible to delete yourself, as there is a second manager in the site (the creator)
        assertTrue(siteMembersPage.isRemoveButtonPresent(user2), "It's possible to remove yourself");
        siteMembersPage.removeUser(user2);

        // Verify it's not possible to remove the second manager (relevant prompt pops up)
        assertFalse(siteMembersPage.isUserCanBeRemoved(user1, siteName), "The user can be removed!");
    }

    /**
     * Delete own. Collaborator
     * 
     * @throws Exception
     */
    @Test(groups = "Sanity", dependsOnMethods = "AONE_15227")
    public void AONE_8264() throws Exception
    {
        /** Start Test */
        testName = getClass().getSimpleName() + uniqueRun;
        // String siteName = getSiteName(testName);

        /** Test Data Setup */
        String user3 = username + 3;
        String itemName = "editedByManager";
        String folderName = "folderEditedByManager";

        ShareUser.login(drone, user3, DEFAULT_PASSWORD);

        // User2 opens Site Dashboard
        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName).render();

        // Navigate to Document Library
        ShareUser.openDocumentLibrary(drone).render();

        // Delete own comment to the document
        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, itemName).render();
        // MNT-5867 - Deleted comments do not go to trashcan(not a bug)
        // After executing AONE_15227 the comments won't get recovered, so will create a comment and then check deleting it
        documentDetailsPage.addComment(itemName).render();
        documentDetailsPage.deleteComment(itemName).render();

        // Delete own document
        documentDetailsPage.delete().render();

        // Delete own comment to the folder
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName);
        // MNT-5867 - Deleted comments do not go to trashcan(not a bug)
        // After executing AONE_15227 the comments won't get recovered, so will create a comment and then check deleting it
        folderDetailsPage.addComment(itemName).render();
        folderDetailsPage.deleteComment(itemName).render();

        // Delete own Folder
        ShareUser.openDocumentLibrary(drone).deleteItem(folderName).render();

        // Navigate to Calendar
        CalendarPage calendarPage = dashboard.getSiteNav().selectCalendarPage().render();

        // Delete own event
        calendarPage.deleteEvent(itemName, CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, MONTH_TAB).render();

        // Navigate to Wiki
        WikiPage wikiPage = dashboard.getSiteNav().selectWikiPage().render();

        // Delete own wiki page
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn();
        wikiPageList.deleteWikiWithConfirm(itemName).render();

        // Navigate to Dicsussions
        DiscussionsPage discussionsPage = dashboard.getSiteNav().selectDiscussionsPage().render();

        // Delete own reply for the topic
        // ALF-18747 - Replies aren't displayed after recovering Discussion from Trashcan
        // Not possible to delete replies until MNT-11398 is implemented

        // Delete own topic
        int expCount = discussionsPage.getTopicsCount() - 1;
        discussionsPage.deleteTopicWithConfirm(itemName).render(6000);
        waitInSeconds(4);
        assertEquals(discussionsPage.getTopicsCount(), expCount);

        // Navigate to Blog
        BlogPage blogPage = dashboard.getSiteNav().selectBlogPage().render();

        // Delete own comment to the post
        // MNT-5867 - Deleted comments do not go to trashcan(not a bug)
        // After executing AONE_15227 the comments won't get recovered, so will create a comment and then check deleting it
        PostViewPage postViewPage = blogPage.openBlogPost(itemName).render();
        postViewPage.createBlogComment(itemName).render();
        postViewPage.deleteCommentWithConfirm(itemName).render();
        waitInSeconds(3);
        assertEquals(postViewPage.getCommentCount(), 0, "Comment wasn't deleted");

        // Delete own blog post
        postViewPage.deleteBlogPostWithConfirm().render();

        // Navigate to Links
        LinksPage linksPage = dashboard.getSiteNav().selectLinksPage().render();

        // Delete own link
        linksPage.deleteLinkWithConfirm(itemName).render();

        // Navigate to Data Lists
        DataListPage dataListPage = dashboard.getSiteNav().selectDataListPage().render();

        // Delete own item of the list
        dataListPage.selectDataList(itemName);
        ContactList contactList = new ContactList(drone).render();
        contactList.deleteAnItemWithConfirm(itemName);

        // Delete own data list
        dataListPage.deleteDataListWithConfirm(itemName).render();
        NewListForm newListForm = new NewListForm(drone);
        if (newListForm.isShareDialogueDisplayed())
        {
            newListForm.clickClose().render();
        }
        // Navigate to Members
        SiteMembersPage siteMembersPage = dashboard.getSiteNav().selectMembersPage().render();

        // Verify it is not possible to delete yourself
        assertFalse(siteMembersPage.isRemoveButtonPresent(user3), "It's possible to remove yourself");
    }

    /**
     * Delete own. Contributor
     * 
     * @throws Exception
     */
    @Test(groups = "Sanity", dependsOnMethods = "AONE_15221")
    public void AONE_8265() throws Exception
    {
        /** Start Test */
        testName = getClass().getSimpleName() + uniqueRun;
        ;
        // String siteName = getSiteName(testName);

        /** Test Data Setup */
        String user4 = username + 4;
        testName += "contributoredited";
        String itemName = "editedByCollaborator";
        String folderName = "folderEditedByCollaborator";

        ShareUser.login(drone, user4, DEFAULT_PASSWORD);

        // User4 opens Site Dashboard
        SiteDashboardPage dashboard = ShareUser.openSiteDashboard(drone, siteName).render();

        // Navigate to Document Library
        ShareUser.openDocumentLibrary(drone).render();

        // Delete own comment to the document
        DocumentDetailsPage documentDetailsPage = ShareUser.openDocumentDetailPage(drone, itemName).render();
        documentDetailsPage.deleteComment(testName);

        // Delete own document
        documentDetailsPage.delete().render();

        // Delete own comment to the folder
        FolderDetailsPage folderDetailsPage = ShareUser.openFolderDetailPage(drone, folderName);
        folderDetailsPage.deleteComment(itemName);

        // Delete own Folder
        ShareUser.openDocumentLibrary(drone).deleteItem(folderName).render();

        // Navigate to Calendar
        CalendarPage calendarPage = dashboard.getSiteNav().selectCalendarPage().render();

        // Delete own event
        calendarPage.deleteEvent(itemName, CalendarPage.EventType.MONTH_TAB_SINGLE_EVENT, MONTH_TAB).render();

        // Navigate to Wiki
        WikiPage wikiPage = dashboard.getSiteNav().selectWikiPage().render();

        // Delete own wiki page
        WikiPageList wikiPageList = wikiPage.clickWikiPageListBtn();
        wikiPageList.deleteWikiWithConfirm(itemName).render();

        // Navigate to Dicsussions
        DiscussionsPage discussionsPage = dashboard.getSiteNav().selectDiscussionsPage().render();

        // Delete own reply for the topic
        // Not possible to delete replies until MNT-11398 is implemented

        // Delete own topic
        int expCount = discussionsPage.getTopicsCount() - 1;
        discussionsPage.deleteTopicWithConfirm(testName).render(6000);
        waitInSeconds(3);
        assertEquals(discussionsPage.getTopicsCount(), expCount);

        // Navigate to Blog
        BlogPage blogPage = dashboard.getSiteNav().selectBlogPage().render();

        // Delete own comment to the post
        PostViewPage postViewPage = blogPage.openBlogPost(itemName).deleteCommentWithConfirm(testName).render();

        // Delete own blog post
        postViewPage.deleteBlogPostWithConfirm().render();

        // Navigate to Links
        LinksPage linksPage = dashboard.getSiteNav().selectLinksPage().render();

        // Delete own link
        linksPage.deleteLinkWithConfirm(itemName).render();

        // Navigate to Data Lists
        DataListPage dataListPage = dashboard.getSiteNav().selectDataListPage().render();

        // Delete own item of the list
        dataListPage.selectDataList(itemName);
        ContactList contactList = new ContactList(drone).render();
        contactList.deleteAnItemWithConfirm(itemName);

        // Delete own data list
        dataListPage.deleteDataListWithConfirm(itemName).render();

        // Navigate to Members
        ShareUser.refreshSharePage(drone);
        SiteMembersPage siteMembersPage = dashboard.getSiteNav().selectMembersPage().render();

        // Verify it is not possible to delete yourself
        assertFalse(siteMembersPage.isRemoveButtonPresent(user4), "It's possible to remove yourself");
    }
}