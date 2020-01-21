package support

import android.R
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.*
import android.graphics.drawable.shapes.OvalShape
import android.os.Build
import android.util.StateSet
import support.component.AndroidUtilities
import java.util.*

class Theme {
    companion object {
        private val defaultColors = HashMap<String, Int>()
        private val fallbackKeys = HashMap<String, String>()
        private val currentColors= HashMap<String, Int>()
        public var dividerPaint: Paint = Paint()
        private val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val wallpaper: Drawable? = null
        private val themedWallpaper: Drawable? = null

        const val key_Tajrish_line = "Tajrish_line"
        const val key_Farhangsara_line = "Farhangsara_line"
        const val key_Azadegan_line = "Azadegan_line"
        const val key_kolahdooz_line = "kolahdooz_line"
        const val key_FarhangsaraSubLine_line = "FarhangsaraSubLine_line"
        const val key_Abdolazim_line = "Abdolazim_line"
        const val key_Takhti_line = "Takhti_line"
        const val key_metro_background = "metro_background"
        const val key_contacts_inviteBackground = "contacts_inviteBackground"
        /*
         const val key_contacts_inviteBackground = "contacts_inviteBackground"
         const val key_avatar_backgroundGreen = "avatar_backgroundGreen"
         const val key_dialogRoundCheckBox = "dialogRoundCheckBox"
         const val key_dialogRoundCheckBoxCheck = "dialogRoundCheckBoxCheck"
         const val key_chat_messagePanelVoicePressed = "chat_messagePanelVoicePressed"
         const val key_windowBackgroundWhiteHintText = "windowBackgroundWhiteHintText"
         const val key_chats_menuTopShadow = "chats_menuTopShadow"*/

        const val key_contacts_inviteText = "contacts_inviteText"
        const val key_dialogBackground = "dialogBackground"
        const val key_dialogBackgroundGray = "dialogBackgroundGray"
        const val key_dialogTextBlack = "dialogTextBlack"
        const val key_dialogTextLink = "dialogTextLink"
        const val key_dialogLinkSelection = "dialogLinkSelection"
        const val key_dialogTextRed = "dialogTextRed"
        const val key_dialogTextBlue = "dialogTextBlue"
        const val key_dialogTextBlue2 = "dialogTextBlue2"
        const val key_dialogTextBlue3 = "dialogTextBlue3"
        const val key_dialogTextBlue4 = "dialogTextBlue4"
        const val key_dialogTextGray = "dialogTextGray"
        const val key_dialogTextGray2 = "dialogTextGray2"
        const val key_dialogTextGray3 = "dialogTextGray3"
        const val key_dialogTextGray4 = "dialogTextGray4"
        const val key_dialogTextHint = "dialogTextHint"
        const val key_dialogInputField = "dialogInputField"
        const val key_dialogInputFieldActivated = "dialogInputFieldActivated"
        const val key_dialogCheckboxSquareBackground = "dialogCheckboxSquareBackground"
        const val key_dialogCheckboxSquareCheck = "dialogCheckboxSquareCheck"
        const val key_dialogCheckboxSquareUnchecked = "dialogCheckboxSquareUnchecked"
        const val key_dialogCheckboxSquareDisabled = "dialogCheckboxSquareDisabled"
        const val key_dialogScrollGlow = "dialogScrollGlow"
        const val key_dialogRoundCheckBox = "dialogRoundCheckBox"
        const val key_dialogRoundCheckBoxCheck = "dialogRoundCheckBoxCheck"
        const val key_dialogBadgeBackground = "dialogBadgeBackground"
        const val key_dialogBadgeText = "dialogBadgeText"
        const val key_dialogRadioBackground = "dialogRadioBackground"
        const val key_dialogRadioBackgroundChecked = "dialogRadioBackgroundChecked"
        const val key_dialogProgressCircle = "dialogProgressCircle"
        const val key_dialogLineProgress = "dialogLineProgress"
        const val key_dialogLineProgressBackground = "dialogLineProgressBackground"
        const val key_dialogButton = "dialogButton"
        const val key_dialogButtonSelector = "dialogButtonSelector"
        const val key_dialogIcon = "dialogIcon"
        const val key_dialogGrayLine = "dialogGrayLine"
        const val key_dialogTopBackground = "dialogTopBackground"

        const val key_windowBackgroundWhite = "windowBackgroundWhite"
        const val key_progressCircle = "progressCircle"
        const val key_listSelector = "listSelectorSDK21"
        const val key_windowBackgroundWhiteInputField = "windowBackgroundWhiteInputField"
        const val key_windowBackgroundWhiteInputFieldActivated = "windowBackgroundWhiteInputFieldActivated"
        const val key_windowBackgroundWhiteGrayIcon = "windowBackgroundWhiteGrayIcon"
        const val key_windowBackgroundWhiteBlueText = "windowBackgroundWhiteBlueText"
        const val key_windowBackgroundWhiteBlueText2 = "windowBackgroundWhiteBlueText2"
        const val key_windowBackgroundWhiteBlueText3 = "windowBackgroundWhiteBlueText3"
        const val key_windowBackgroundWhiteBlueText4 = "windowBackgroundWhiteBlueText4"
        const val key_windowBackgroundWhiteBlueText5 = "windowBackgroundWhiteBlueText5"
        const val key_windowBackgroundWhiteBlueText6 = "windowBackgroundWhiteBlueText6"
        const val key_windowBackgroundWhiteBlueText7 = "windowBackgroundWhiteBlueText7"
        const val key_windowBackgroundWhiteGreenText = "windowBackgroundWhiteGreenText"
        const val key_windowBackgroundWhiteGreenText2 = "windowBackgroundWhiteGreenText2"
        const val key_windowBackgroundWhiteRedText = "windowBackgroundWhiteRedText"
        const val key_windowBackgroundWhiteRedText2 = "windowBackgroundWhiteRedText2"
        const val key_windowBackgroundWhiteRedText3 = "windowBackgroundWhiteRedText3"
        const val key_windowBackgroundWhiteRedText4 = "windowBackgroundWhiteRedText4"
        const val key_windowBackgroundWhiteRedText5 = "windowBackgroundWhiteRedText5"
        const val key_windowBackgroundWhiteRedText6 = "windowBackgroundWhiteRedText6"
        const val key_windowBackgroundWhiteGrayText = "windowBackgroundWhiteGrayText"
        const val key_windowBackgroundWhiteGrayText2 = "windowBackgroundWhiteGrayText2"
        const val key_windowBackgroundWhiteGrayText3 = "windowBackgroundWhiteGrayText3"
        const val key_windowBackgroundWhiteGrayText4 = "windowBackgroundWhiteGrayText4"
        const val key_windowBackgroundWhiteGrayText5 = "windowBackgroundWhiteGrayText5"
        const val key_windowBackgroundWhiteGrayText6 = "windowBackgroundWhiteGrayText6"
        const val key_windowBackgroundWhiteGrayText7 = "windowBackgroundWhiteGrayText7"
        const val key_windowBackgroundWhiteGrayText8 = "windowBackgroundWhiteGrayText8"
        const val key_windowBackgroundWhiteGrayLine = "windowBackgroundWhiteGrayLine"
        const val key_windowBackgroundWhiteBlackText = "windowBackgroundWhiteBlackText"
        const val key_windowBackgroundWhiteHintText = "windowBackgroundWhiteHintText"
        const val key_windowBackgroundWhiteValueText = "windowBackgroundWhiteValueText"
        const val key_windowBackgroundWhiteLinkText = "windowBackgroundWhiteLinkText"
        const val key_windowBackgroundWhiteLinkSelection = "windowBackgroundWhiteLinkSelection"
        const val key_windowBackgroundWhiteBlueHeader = "windowBackgroundWhiteBlueHeader"
        const val key_switchThumb = "switchThumb"
        const val key_switchTrack = "switchTrack"
        const val key_switchThumbChecked = "switchThumbChecked"
        const val key_switchTrackChecked = "switchTrackChecked"
        const val key_checkboxSquareBackground = "checkboxSquareBackground"
        const val key_checkboxSquareCheck = "checkboxSquareCheck"
        const val key_checkboxSquareUnchecked = "checkboxSquareUnchecked"
        const val key_checkboxSquareDisabled = "checkboxSquareDisabled"
        const val key_windowBackgroundGray = "windowBackgroundGray"
        const val key_windowBackgroundGrayShadow = "windowBackgroundGrayShadow"
        const val key_emptyListPlaceholder = "emptyListPlaceholder"
        const val key_divider = "divider"
        const val key_graySection = "graySection"
        const val key_radioBackground = "radioBackground"
        const val key_radioBackgroundChecked = "radioBackgroundChecked"
        const val key_checkbox = "checkbox"
        const val key_checkboxCheck = "checkboxCheck"
        const val key_fastScrollActive = "fastScrollActive"
        const val key_fastScrollInactive = "fastScrollInactive"
        const val key_fastScrollText = "fastScrollText"

        const val key_inappPlayerPerformer = "inappPlayerPerformer"
        const val key_inappPlayerTitle = "inappPlayerTitle"
        const val key_inappPlayerBackground = "inappPlayerBackground"
        const val key_inappPlayerPlayPause = "inappPlayerPlayPause"
        const val key_inappPlayerClose = "inappPlayerClose"

        const val key_returnToCallBackground = "returnToCallBackground"
        const val key_returnToCallText = "returnToCallText"

        const val key_contextProgressInner1 = "contextProgressInner1"
        const val key_contextProgressOuter1 = "contextProgressOuter1"
        const val key_contextProgressInner2 = "contextProgressInner2"
        const val key_contextProgressOuter2 = "contextProgressOuter2"
        const val key_contextProgressInner3 = "contextProgressInner3"
        const val key_contextProgressOuter3 = "contextProgressOuter3"

        const val key_avatar_text = "avatar_text"
        const val key_avatar_backgroundSaved = "avatar_backgroundSaved"
        const val key_avatar_backgroundRed = "avatar_backgroundRed"
        const val key_avatar_backgroundOrange = "avatar_backgroundOrange"
        const val key_avatar_backgroundViolet = "avatar_backgroundViolet"
        const val key_avatar_backgroundGreen = "avatar_backgroundGreen"
        const val key_avatar_backgroundCyan = "avatar_backgroundCyan"
        const val key_avatar_backgroundBlue = "avatar_backgroundBlue"
        const val key_avatar_backgroundPink = "avatar_backgroundPink"
        const val key_avatar_backgroundGroupCreateSpanBlue = "avatar_backgroundGroupCreateSpanBlue"
        const val key_avatar_backgroundInProfileRed = "avatar_backgroundInProfileRed"
        const val key_avatar_backgroundInProfileOrange = "avatar_backgroundInProfileOrange"
        const val key_avatar_backgroundInProfileViolet = "avatar_backgroundInProfileViolet"
        const val key_avatar_backgroundInProfileGreen = "avatar_backgroundInProfileGreen"
        const val key_avatar_backgroundInProfileCyan = "avatar_backgroundInProfileCyan"
        const val key_avatar_backgroundInProfileBlue = "avatar_backgroundInProfileBlue"
        const val key_avatar_backgroundInProfilePink = "avatar_backgroundInProfilePink"
        const val key_avatar_backgroundActionBarRed = "avatar_backgroundActionBarRed"
        const val key_avatar_backgroundActionBarOrange = "avatar_backgroundActionBarOrange"
        const val key_avatar_backgroundActionBarViolet = "avatar_backgroundActionBarViolet"
        const val key_avatar_backgroundActionBarGreen = "avatar_backgroundActionBarGreen"
        const val key_avatar_backgroundActionBarCyan = "avatar_backgroundActionBarCyan"
        const val key_avatar_backgroundActionBarBlue = "avatar_backgroundActionBarBlue"
        const val key_avatar_backgroundActionBarPink = "avatar_backgroundActionBarPink"
        const val key_avatar_subtitleInProfileRed = "avatar_subtitleInProfileRed"
        const val key_avatar_subtitleInProfileOrange = "avatar_subtitleInProfileOrange"
        const val key_avatar_subtitleInProfileViolet = "avatar_subtitleInProfileViolet"
        const val key_avatar_subtitleInProfileGreen = "avatar_subtitleInProfileGreen"
        const val key_avatar_subtitleInProfileCyan = "avatar_subtitleInProfileCyan"
        const val key_avatar_subtitleInProfileBlue = "avatar_subtitleInProfileBlue"
        const val key_avatar_subtitleInProfilePink = "avatar_subtitleInProfilePink"
        const val key_avatar_nameInMessageRed = "avatar_nameInMessageRed"
        const val key_avatar_nameInMessageOrange = "avatar_nameInMessageOrange"
        const val key_avatar_nameInMessageViolet = "avatar_nameInMessageViolet"
        const val key_avatar_nameInMessageGreen = "avatar_nameInMessageGreen"
        const val key_avatar_nameInMessageCyan = "avatar_nameInMessageCyan"
        const val key_avatar_nameInMessageBlue = "avatar_nameInMessageBlue"
        const val key_avatar_nameInMessagePink = "avatar_nameInMessagePink"
        const val key_avatar_actionBarSelectorRed = "avatar_actionBarSelectorRed"
        const val key_avatar_actionBarSelectorOrange = "avatar_actionBarSelectorOrange"
        const val key_avatar_actionBarSelectorViolet = "avatar_actionBarSelectorViolet"
        const val key_avatar_actionBarSelectorGreen = "avatar_actionBarSelectorGreen"
        const val key_avatar_actionBarSelectorCyan = "avatar_actionBarSelectorCyan"
        const val key_avatar_actionBarSelectorBlue = "avatar_actionBarSelectorBlue"
        const val key_avatar_actionBarSelectorPink = "avatar_actionBarSelectorPink"
        const val key_avatar_actionBarIconRed = "avatar_actionBarIconRed"
        const val key_avatar_actionBarIconOrange = "avatar_actionBarIconOrange"
        const val key_avatar_actionBarIconViolet = "avatar_actionBarIconViolet"
        const val key_avatar_actionBarIconGreen = "avatar_actionBarIconGreen"
        const val key_avatar_actionBarIconCyan = "avatar_actionBarIconCyan"
        const val key_avatar_actionBarIconBlue = "avatar_actionBarIconBlue"
        const val key_avatar_actionBarIconPink = "avatar_actionBarIconPink"

        var keys_avatar_background = arrayOf(key_avatar_backgroundRed, key_avatar_backgroundOrange, key_avatar_backgroundViolet, key_avatar_backgroundGreen, key_avatar_backgroundCyan, key_avatar_backgroundBlue, key_avatar_backgroundPink)
        var keys_avatar_backgroundInProfile = arrayOf(key_avatar_backgroundInProfileRed, key_avatar_backgroundInProfileOrange, key_avatar_backgroundInProfileViolet, key_avatar_backgroundInProfileGreen, key_avatar_backgroundInProfileCyan, key_avatar_backgroundInProfileBlue, key_avatar_backgroundInProfilePink)
        var keys_avatar_backgroundActionBar = arrayOf(key_avatar_backgroundActionBarRed, key_avatar_backgroundActionBarOrange, key_avatar_backgroundActionBarViolet, key_avatar_backgroundActionBarGreen, key_avatar_backgroundActionBarCyan, key_avatar_backgroundActionBarBlue, key_avatar_backgroundActionBarPink)
        var keys_avatar_subtitleInProfile = arrayOf(key_avatar_subtitleInProfileRed, key_avatar_subtitleInProfileOrange, key_avatar_subtitleInProfileViolet, key_avatar_subtitleInProfileGreen, key_avatar_subtitleInProfileCyan, key_avatar_subtitleInProfileBlue, key_avatar_subtitleInProfilePink)
        var keys_avatar_nameInMessage = arrayOf(key_avatar_nameInMessageRed, key_avatar_nameInMessageOrange, key_avatar_nameInMessageViolet, key_avatar_nameInMessageGreen, key_avatar_nameInMessageCyan, key_avatar_nameInMessageBlue, key_avatar_nameInMessagePink)
        var keys_avatar_actionBarSelector = arrayOf(key_avatar_actionBarSelectorRed, key_avatar_actionBarSelectorOrange, key_avatar_actionBarSelectorViolet, key_avatar_actionBarSelectorGreen, key_avatar_actionBarSelectorCyan, key_avatar_actionBarSelectorBlue, key_avatar_actionBarSelectorPink)
        var keys_avatar_actionBarIcon = arrayOf(key_avatar_actionBarIconRed, key_avatar_actionBarIconOrange, key_avatar_actionBarIconViolet, key_avatar_actionBarIconGreen, key_avatar_actionBarIconCyan, key_avatar_actionBarIconBlue, key_avatar_actionBarIconPink)

        const val key_actionBarDefault = "actionBarDefault"
        const val key_actionBarDefaultSelector = "actionBarDefaultSelector"
        const val key_actionBarWhiteSelector = "actionBarWhiteSelector"
        const val key_actionBarDefaultIcon = "actionBarDefaultIcon"
        const val key_actionBarActionModeDefault = "actionBarActionModeDefault"
        const val key_actionBarActionModeDefaultTop = "actionBarActionModeDefaultTop"
        const val key_actionBarActionModeDefaultIcon = "actionBarActionModeDefaultIcon"
        const val key_actionBarActionModeDefaultSelector = "actionBarActionModeDefaultSelector"
        const val key_actionBarDefaultTitle = "actionBarDefaultTitle"
        const val key_actionBarDefaultSubtitle = "actionBarDefaultSubtitle"
        const val key_actionBarDefaultSearch = "actionBarDefaultSearch"
        const val key_actionBarDefaultSearchPlaceholder = "actionBarDefaultSearchPlaceholder"
        const val key_actionBarDefaultSubmenuItem = "actionBarDefaultSubmenuItem"
        const val key_actionBarDefaultSubmenuBackground = "actionBarDefaultSubmenuBackground"
        const val key_chats_unreadCounter = "chats_unreadCounter"
        const val key_chats_unreadCounterMuted = "chats_unreadCounterMuted"
        const val key_chats_unreadCounterText = "chats_unreadCounterText"
        const val key_chats_name = "chats_name"
        const val key_chats_secretName = "chats_secretName"
        const val key_chats_secretIcon = "chats_secretIcon"
        const val key_chats_nameIcon = "chats_nameIcon"
        const val key_chats_pinnedIcon = "chats_pinnedIcon"
        const val key_chats_message = "chats_message"
        const val key_chats_draft = "chats_draft"
        const val key_chats_nameMessage = "chats_nameMessage"
        const val key_chats_attachMessage = "chats_attachMessage"
        const val key_chats_actionMessage = "chats_actionMessage"
        const val key_chats_date = "chats_date"
        const val key_chats_pinnedOverlay = "chats_pinnedOverlay"
        const val key_chats_tabletSelectedOverlay = "chats_tabletSelectedOverlay"
        const val key_chats_sentCheck = "chats_sentCheck"
        const val key_chats_sentClock = "chats_sentClock"
        const val key_chats_sentError = "chats_sentError"
        const val key_chats_sentErrorIcon = "chats_sentErrorIcon"
        const val key_chats_verifiedBackground = "chats_verifiedBackground"
        const val key_chats_verifiedCheck = "chats_verifiedCheck"
        const val key_chats_muteIcon = "chats_muteIcon"
        const val key_chats_menuTopShadow = "chats_menuTopShadow"
        const val key_chats_menuBackground = "chats_menuBackground"
        const val key_chats_menuItemText = "chats_menuItemText"
        const val key_chats_menuItemIcon = "chats_menuItemIcon"
        const val key_chats_menuName = "chats_menuName"
        const val key_chats_menuPhone = "chats_menuPhone"
        const val key_chats_menuPhoneCats = "chats_menuPhoneCats"
        const val key_chats_menuCloud = "chats_menuCloud"
        const val key_chats_menuCloudBackgroundCats = "chats_menuCloudBackgroundCats"
        const val key_chats_actionIcon = "chats_actionIcon"
        const val key_chats_actionBackground = "chats_actionBackground"
        const val key_chats_actionPressedBackground = "chats_actionPressedBackground"

        const val key_chat_inBubble = "chat_inBubble"
        const val key_chat_inBubbleSelected = "chat_inBubbleSelected"
        const val key_chat_inBubbleShadow = "chat_inBubbleShadow"
        const val key_chat_outBubble = "chat_outBubble"
        const val key_chat_outBubbleSelected = "chat_outBubbleSelected"
        const val key_chat_outBubbleShadow = "chat_outBubbleShadow"
        const val key_chat_messageTextIn = "chat_messageTextIn"
        const val key_chat_messageTextOut = "chat_messageTextOut"
        const val key_chat_messageLinkIn = "chat_messageLinkIn"
        const val key_chat_messageLinkOut = "chat_messageLinkOut"
        const val key_chat_serviceText = "chat_serviceText"
        const val key_chat_serviceLink = "chat_serviceLink"
        const val key_chat_serviceIcon = "chat_serviceIcon"
        const val key_chat_serviceBackground = "chat_serviceBackground"
        const val key_chat_serviceBackgroundSelected = "chat_serviceBackgroundSelected"
        const val key_chat_muteIcon = "chat_muteIcon"
        const val key_chat_lockIcon = "chat_lockIcon"
        const val key_chat_outSentCheck = "chat_outSentCheck"
        const val key_chat_outSentCheckSelected = "chat_outSentCheckSelected"
        const val key_chat_outSentClock = "chat_outSentClock"
        const val key_chat_outSentClockSelected = "chat_outSentClockSelected"
        const val key_chat_inSentClock = "chat_inSentClock"
        const val key_chat_inSentClockSelected = "chat_inSentClockSelected"
        const val key_chat_mediaSentCheck = "chat_mediaSentCheck"
        const val key_chat_mediaSentClock = "chat_mediaSentClock"
        const val key_chat_mediaTimeBackground = "chat_mediaTimeBackground"
        const val key_chat_outViews = "chat_outViews"
        const val key_chat_outViewsSelected = "chat_outViewsSelected"
        const val key_chat_inViews = "chat_inViews"
        const val key_chat_inViewsSelected = "chat_inViewsSelected"
        const val key_chat_mediaViews = "chat_mediaViews"
        const val key_chat_outMenu = "chat_outMenu"
        const val key_chat_outMenuSelected = "chat_outMenuSelected"
        const val key_chat_inMenu = "chat_inMenu"
        const val key_chat_inMenuSelected = "chat_inMenuSelected"
        const val key_chat_mediaMenu = "chat_mediaMenu"
        const val key_chat_outInstant = "chat_outInstant"
        const val key_chat_outInstantSelected = "chat_outInstantSelected"
        const val key_chat_inInstant = "chat_inInstant"
        const val key_chat_inInstantSelected = "chat_inInstantSelected"
        const val key_chat_sentError = "chat_sentError"
        const val key_chat_sentErrorIcon = "chat_sentErrorIcon"
        const val key_chat_selectedBackground = "chat_selectedBackground"
        const val key_chat_previewDurationText = "chat_previewDurationText"
        const val key_chat_previewGameText = "chat_previewGameText"
        const val key_chat_inPreviewInstantText = "chat_inPreviewInstantText"
        const val key_chat_outPreviewInstantText = "chat_outPreviewInstantText"
        const val key_chat_inPreviewInstantSelectedText = "chat_inPreviewInstantSelectedText"
        const val key_chat_outPreviewInstantSelectedText = "chat_outPreviewInstantSelectedText"
        const val key_chat_secretTimeText = "chat_secretTimeText"
        const val key_chat_stickerNameText = "chat_stickerNameText"
        const val key_chat_botButtonText = "chat_botButtonText"
        const val key_chat_botProgress = "chat_botProgress"
        const val key_chat_inForwardedNameText = "chat_inForwardedNameText"
        const val key_chat_outForwardedNameText = "chat_outForwardedNameText"
        const val key_chat_inViaBotNameText = "chat_inViaBotNameText"
        const val key_chat_outViaBotNameText = "chat_outViaBotNameText"
        const val key_chat_stickerViaBotNameText = "chat_stickerViaBotNameText"
        const val key_chat_inReplyLine = "chat_inReplyLine"
        const val key_chat_outReplyLine = "chat_outReplyLine"
        const val key_chat_stickerReplyLine = "chat_stickerReplyLine"
        const val key_chat_inReplyNameText = "chat_inReplyNameText"
        const val key_chat_outReplyNameText = "chat_outReplyNameText"
        const val key_chat_stickerReplyNameText = "chat_stickerReplyNameText"
        const val key_chat_inReplyMessageText = "chat_inReplyMessageText"
        const val key_chat_outReplyMessageText = "chat_outReplyMessageText"
        const val key_chat_inReplyMediaMessageText = "chat_inReplyMediaMessageText"
        const val key_chat_outReplyMediaMessageText = "chat_outReplyMediaMessageText"
        const val key_chat_inReplyMediaMessageSelectedText = "chat_inReplyMediaMessageSelectedText"
        const val key_chat_outReplyMediaMessageSelectedText = "chat_outReplyMediaMessageSelectedText"
        const val key_chat_stickerReplyMessageText = "chat_stickerReplyMessageText"
        const val key_chat_inPreviewLine = "chat_inPreviewLine"
        const val key_chat_outPreviewLine = "chat_outPreviewLine"
        const val key_chat_inSiteNameText = "chat_inSiteNameText"
        const val key_chat_outSiteNameText = "chat_outSiteNameText"
        const val key_chat_inContactNameText = "chat_inContactNameText"
        const val key_chat_outContactNameText = "chat_outContactNameText"
        const val key_chat_inContactPhoneText = "chat_inContactPhoneText"
        const val key_chat_outContactPhoneText = "chat_outContactPhoneText"
        const val key_chat_mediaProgress = "chat_mediaProgress"
        const val key_chat_inAudioProgress = "chat_inAudioProgress"
        const val key_chat_outAudioProgress = "chat_outAudioProgress"
        const val key_chat_inAudioSelectedProgress = "chat_inAudioSelectedProgress"
        const val key_chat_outAudioSelectedProgress = "chat_outAudioSelectedProgress"
        const val key_chat_mediaTimeText = "chat_mediaTimeText"
        const val key_chat_adminText = "chat_adminText"
        const val key_chat_adminSelectedText = "chat_adminSelectedText"
        const val key_chat_inTimeText = "chat_inTimeText"
        const val key_chat_outTimeText = "chat_outTimeText"
        const val key_chat_inTimeSelectedText = "chat_inTimeSelectedText"
        const val key_chat_outTimeSelectedText = "chat_outTimeSelectedText"
        const val key_chat_inAudioPerfomerText = "chat_inAudioPerfomerText"
        const val key_chat_outAudioPerfomerText = "chat_outAudioPerfomerText"
        const val key_chat_inAudioTitleText = "chat_inAudioTitleText"
        const val key_chat_outAudioTitleText = "chat_outAudioTitleText"
        const val key_chat_inAudioDurationText = "chat_inAudioDurationText"
        const val key_chat_outAudioDurationText = "chat_outAudioDurationText"
        const val key_chat_inAudioDurationSelectedText = "chat_inAudioDurationSelectedText"
        const val key_chat_outAudioDurationSelectedText = "chat_outAudioDurationSelectedText"
        const val key_chat_inAudioSeekbar = "chat_inAudioSeekbar"
        const val key_chat_outAudioSeekbar = "chat_outAudioSeekbar"
        const val key_chat_inAudioSeekbarSelected = "chat_inAudioSeekbarSelected"
        const val key_chat_outAudioSeekbarSelected = "chat_outAudioSeekbarSelected"
        const val key_chat_inAudioSeekbarFill = "chat_inAudioSeekbarFill"
        const val key_chat_outAudioSeekbarFill = "chat_outAudioSeekbarFill"
        const val key_chat_inVoiceSeekbar = "chat_inVoiceSeekbar"
        const val key_chat_outVoiceSeekbar = "chat_outVoiceSeekbar"
        const val key_chat_inVoiceSeekbarSelected = "chat_inVoiceSeekbarSelected"
        const val key_chat_outVoiceSeekbarSelected = "chat_outVoiceSeekbarSelected"
        const val key_chat_inVoiceSeekbarFill = "chat_inVoiceSeekbarFill"
        const val key_chat_outVoiceSeekbarFill = "chat_outVoiceSeekbarFill"
        const val key_chat_inFileProgress = "chat_inFileProgress"
        const val key_chat_outFileProgress = "chat_outFileProgress"
        const val key_chat_inFileProgressSelected = "chat_inFileProgressSelected"
        const val key_chat_outFileProgressSelected = "chat_outFileProgressSelected"
        const val key_chat_inFileNameText = "chat_inFileNameText"
        const val key_chat_outFileNameText = "chat_outFileNameText"
        const val key_chat_inFileInfoText = "chat_inFileInfoText"
        const val key_chat_outFileInfoText = "chat_outFileInfoText"
        const val key_chat_inFileInfoSelectedText = "chat_inFileInfoSelectedText"
        const val key_chat_outFileInfoSelectedText = "chat_outFileInfoSelectedText"
        const val key_chat_inFileBackground = "chat_inFileBackground"
        const val key_chat_outFileBackground = "chat_outFileBackground"
        const val key_chat_inFileBackgroundSelected = "chat_inFileBackgroundSelected"
        const val key_chat_outFileBackgroundSelected = "chat_outFileBackgroundSelected"
        const val key_chat_inVenueNameText = "chat_inVenueNameText"
        const val key_chat_outVenueNameText = "chat_outVenueNameText"
        const val key_chat_inVenueInfoText = "chat_inVenueInfoText"
        const val key_chat_outVenueInfoText = "chat_outVenueInfoText"
        const val key_chat_inVenueInfoSelectedText = "chat_inVenueInfoSelectedText"
        const val key_chat_outVenueInfoSelectedText = "chat_outVenueInfoSelectedText"
        const val key_chat_mediaInfoText = "chat_mediaInfoText"
        const val key_chat_linkSelectBackground = "chat_linkSelectBackground"
        const val key_chat_textSelectBackground = "chat_textSelectBackground"
        const val key_chat_wallpaper = "chat_wallpaper"
        const val key_chat_messagePanelBackground = "chat_messagePanelBackground"
        const val key_chat_messagePanelShadow = "chat_messagePanelShadow"
        const val key_chat_messagePanelText = "chat_messagePanelText"
        const val key_chat_messagePanelHint = "chat_messagePanelHint"
        const val key_chat_messagePanelIcons = "chat_messagePanelIcons"
        const val key_chat_messagePanelSend = "chat_messagePanelSend"
        const val key_chat_messagePanelVoiceLock = "key_chat_messagePanelVoiceLock"
        const val key_chat_messagePanelVoiceLockBackground = "key_chat_messagePanelVoiceLockBackground"
        const val key_chat_messagePanelVoiceLockShadow = "key_chat_messagePanelVoiceLockShadow"
        const val key_chat_topPanelBackground = "chat_topPanelBackground"
        const val key_chat_topPanelClose = "chat_topPanelClose"
        const val key_chat_topPanelLine = "chat_topPanelLine"
        const val key_chat_topPanelTitle = "chat_topPanelTitle"
        const val key_chat_topPanelMessage = "chat_topPanelMessage"
        const val key_chat_reportSpam = "chat_reportSpam"
        const val key_chat_addContact = "chat_addContact"
        const val key_chat_inLoader = "chat_inLoader"
        const val key_chat_inLoaderSelected = "chat_inLoaderSelected"
        const val key_chat_outLoader = "chat_outLoader"
        const val key_chat_outLoaderSelected = "chat_outLoaderSelected"
        const val key_chat_inLoaderPhoto = "chat_inLoaderPhoto"
        const val key_chat_inLoaderPhotoSelected = "chat_inLoaderPhotoSelected"
        const val key_chat_inLoaderPhotoIcon = "chat_inLoaderPhotoIcon"
        const val key_chat_inLoaderPhotoIconSelected = "chat_inLoaderPhotoIconSelected"
        const val key_chat_outLoaderPhoto = "chat_outLoaderPhoto"
        const val key_chat_outLoaderPhotoSelected = "chat_outLoaderPhotoSelected"
        const val key_chat_outLoaderPhotoIcon = "chat_outLoaderPhotoIcon"
        const val key_chat_outLoaderPhotoIconSelected = "chat_outLoaderPhotoIconSelected"
        const val key_chat_mediaLoaderPhoto = "chat_mediaLoaderPhoto"
        const val key_chat_mediaLoaderPhotoSelected = "chat_mediaLoaderPhotoSelected"
        const val key_chat_mediaLoaderPhotoIcon = "chat_mediaLoaderPhotoIcon"
        const val key_chat_mediaLoaderPhotoIconSelected = "chat_mediaLoaderPhotoIconSelected"
        const val key_chat_inLocationBackground = "chat_inLocationBackground"
        const val key_chat_inLocationIcon = "chat_inLocationIcon"
        const val key_chat_outLocationBackground = "chat_outLocationBackground"
        const val key_chat_outLocationIcon = "chat_outLocationIcon"
        const val key_chat_inContactBackground = "chat_inContactBackground"
        const val key_chat_inContactIcon = "chat_inContactIcon"
        const val key_chat_outContactBackground = "chat_outContactBackground"
        const val key_chat_outContactIcon = "chat_outContactIcon"
        const val key_chat_inFileIcon = "chat_inFileIcon"
        const val key_chat_inFileSelectedIcon = "chat_inFileSelectedIcon"
        const val key_chat_outFileIcon = "chat_outFileIcon"
        const val key_chat_outFileSelectedIcon = "chat_outFileSelectedIcon"
        const val key_chat_replyPanelIcons = "chat_replyPanelIcons"
        const val key_chat_replyPanelClose = "chat_replyPanelClose"
        const val key_chat_replyPanelName = "chat_replyPanelName"
        const val key_chat_replyPanelMessage = "chat_replyPanelMessage"
        const val key_chat_replyPanelLine = "chat_replyPanelLine"
        const val key_chat_searchPanelIcons = "chat_searchPanelIcons"
        const val key_chat_searchPanelText = "chat_searchPanelText"
        const val key_chat_secretChatStatusText = "chat_secretChatStatusText"
        const val key_chat_fieldOverlayText = "chat_fieldOverlayText"
        const val key_chat_stickersHintPanel = "chat_stickersHintPanel"
        const val key_chat_botSwitchToInlineText = "chat_botSwitchToInlineText"
        const val key_chat_unreadMessagesStartArrowIcon = "chat_unreadMessagesStartArrowIcon"
        const val key_chat_unreadMessagesStartText = "chat_unreadMessagesStartText"
        const val key_chat_unreadMessagesStartBackground = "chat_unreadMessagesStartBackground"
        const val key_chat_inlineResultIcon = "chat_inlineResultIcon"
        const val key_chat_emojiPanelBackground = "chat_emojiPanelBackground"
        const val key_chat_emojiPanelShadowLine = "chat_emojiPanelShadowLine"
        const val key_chat_emojiPanelEmptyText = "chat_emojiPanelEmptyText"
        const val key_chat_emojiPanelIcon = "chat_emojiPanelIcon"
        const val key_chat_emojiPanelIconSelected = "chat_emojiPanelIconSelected"
        const val key_chat_emojiPanelStickerPackSelector = "chat_emojiPanelStickerPackSelector"
        const val key_chat_emojiPanelIconSelector = "chat_emojiPanelIconSelector"
        const val key_chat_emojiPanelBackspace = "chat_emojiPanelBackspace"
        const val key_chat_emojiPanelMasksIcon = "chat_emojiPanelMasksIcon"
        const val key_chat_emojiPanelMasksIconSelected = "chat_emojiPanelMasksIconSelected"
        const val key_chat_emojiPanelTrendingTitle = "chat_emojiPanelTrendingTitle"
        const val key_chat_emojiPanelStickerSetName = "chat_emojiPanelStickerSetName"
        const val key_chat_emojiPanelStickerSetNameIcon = "chat_emojiPanelStickerSetNameIcon"
        const val key_chat_emojiPanelTrendingDescription = "chat_emojiPanelTrendingDescription"
        const val key_chat_botKeyboardButtonText = "chat_botKeyboardButtonText"
        const val key_chat_botKeyboardButtonBackground = "chat_botKeyboardButtonBackground"
        const val key_chat_botKeyboardButtonBackgroundPressed = "chat_botKeyboardButtonBackgroundPressed"
        const val key_chat_emojiPanelNewTrending = "chat_emojiPanelNewTrending"
        const val key_chat_editDoneIcon = "chat_editDoneIcon"
        const val key_chat_messagePanelVoicePressed = "chat_messagePanelVoicePressed"
        const val key_chat_messagePanelVoiceBackground = "chat_messagePanelVoiceBackground"
        const val key_chat_messagePanelVoiceShadow = "chat_messagePanelVoiceShadow"
        const val key_chat_messagePanelVoiceDelete = "chat_messagePanelVoiceDelete"
        const val key_chat_messagePanelVoiceDuration = "chat_messagePanelVoiceDuration"
        const val key_chat_recordedVoicePlayPause = "chat_recordedVoicePlayPause"
        const val key_chat_recordedVoicePlayPausePressed = "chat_recordedVoicePlayPausePressed"
        const val key_chat_recordedVoiceProgress = "chat_recordedVoiceProgress"
        const val key_chat_recordedVoiceProgressInner = "chat_recordedVoiceProgressInner"
        const val key_chat_recordedVoiceDot = "chat_recordedVoiceDot"
        const val key_chat_recordedVoiceBackground = "chat_recordedVoiceBackground"
        const val key_chat_recordVoiceCancel = "chat_recordVoiceCancel"
        const val key_chat_recordTime = "chat_recordTime"
        const val key_chat_messagePanelCancelInlineBot = "chat_messagePanelCancelInlineBot"
        const val key_chat_gifSaveHintText = "chat_gifSaveHintText"
        const val key_chat_gifSaveHintBackground = "chat_gifSaveHintBackground"
        const val key_chat_goDownButton = "chat_goDownButton"
        const val key_chat_goDownButtonShadow = "chat_goDownButtonShadow"
        const val key_chat_goDownButtonIcon = "chat_goDownButtonIcon"
        const val key_chat_goDownButtonCounter = "chat_goDownButtonCounter"
        const val key_chat_goDownButtonCounterBackground = "chat_goDownButtonCounterBackground"
        const val key_chat_secretTimerBackground = "chat_secretTimerBackground"
        const val key_chat_secretTimerText = "chat_secretTimerText"

        const val key_profile_creatorIcon = "profile_creatorIcon"
        const val key_profile_adminIcon = "profile_adminIcon"
        const val key_profile_title = "profile_title"
        const val key_profile_actionIcon = "profile_actionIcon"
        const val key_profile_actionBackground = "profile_actionBackground"
        const val key_profile_actionPressedBackground = "profile_actionPressedBackground"
        const val key_profile_verifiedBackground = "profile_verifiedBackground"
        const val key_profile_verifiedCheck = "profile_verifiedCheck"

        const val key_sharedMedia_startStopLoadIcon = "sharedMedia_startStopLoadIcon"
        const val key_sharedMedia_linkPlaceholder = "sharedMedia_linkPlaceholder"
        const val key_sharedMedia_linkPlaceholderText = "sharedMedia_linkPlaceholderText"

        const val key_featuredStickers_addedIcon = "featuredStickers_addedIcon"
        const val key_featuredStickers_buttonProgress = "featuredStickers_buttonProgress"
        const val key_featuredStickers_addButton = "featuredStickers_addButton"
        const val key_featuredStickers_addButtonPressed = "featuredStickers_addButtonPressed"
        const val key_featuredStickers_delButton = "featuredStickers_delButton"
        const val key_featuredStickers_delButtonPressed = "featuredStickers_delButtonPressed"
        const val key_featuredStickers_buttonText = "featuredStickers_buttonText"
        const val key_featuredStickers_unread = "featuredStickers_unread"

        const val key_stickers_menu = "stickers_menu"
        const val key_stickers_menuSelector = "stickers_menuSelector"

        const val key_changephoneinfo_image = "changephoneinfo_image"

        const val key_groupcreate_hintText = "groupcreate_hintText"
        const val key_groupcreate_cursor = "groupcreate_cursor"
        const val key_groupcreate_sectionShadow = "groupcreate_sectionShadow"
        const val key_groupcreate_sectionText = "groupcreate_sectionText"
        const val key_groupcreate_onlineText = "groupcreate_onlineText"
        const val key_groupcreate_offlineText = "groupcreate_offlineText"
        const val key_groupcreate_checkbox = "groupcreate_checkbox"
        const val key_groupcreate_checkboxCheck = "groupcreate_checkboxCheck"
        const val key_groupcreate_spanText = "groupcreate_spanText"
        const val key_groupcreate_spanBackground = "groupcreate_spanBackground"

        const val key_login_progressInner = "login_progressInner"
        const val key_login_progressOuter = "login_progressOuter"

        const val key_musicPicker_checkbox = "musicPicker_checkbox"
        const val key_musicPicker_checkboxCheck = "musicPicker_checkboxCheck"
        const val key_musicPicker_buttonBackground = "musicPicker_buttonBackground"
        const val key_musicPicker_buttonIcon = "musicPicker_buttonIcon"

        const val key_picker_enabledButton = "picker_enabledButton"
        const val key_picker_disabledButton = "picker_disabledButton"
        const val key_picker_badge = "picker_badge"
        const val key_picker_badgeText = "picker_badgeText"

        const val key_location_markerX = "location_markerX"
        const val key_location_sendLocationBackground = "location_sendLocationBackground"
        const val key_location_sendLiveLocationBackground = "location_sendLiveLocationBackground"
        const val key_location_sendLocationIcon = "location_sendLocationIcon"
        const val key_location_liveLocationProgress = "location_liveLocationProgress"
        const val key_location_placeLocationBackground = "location_placeLocationBackground"
        const val key_dialog_liveLocationProgress = "location_liveLocationProgress"

        const val key_files_folderIcon = "files_folderIcon"
        const val key_files_folderIconBackground = "files_folderIconBackground"
        const val key_files_iconText = "files_iconText"

        const val key_sessions_devicesImage = "sessions_devicesImage"

        const val key_calls_callReceivedGreenIcon = "calls_callReceivedGreenIcon"
        const val key_calls_callReceivedRedIcon = "calls_callReceivedRedIcon"

        const val key_calls_ratingStar = "calls_ratingStar"
        const val key_calls_ratingStarSelected = "calls_ratingStarSelected"

        //ununsed
        const val key_chat_outBroadcast = "chat_outBroadcast"
        const val key_chat_mediaBroadcast = "chat_mediaBroadcast"

        const val key_player_actionBar = "player_actionBar"
        const val key_player_actionBarSelector = "player_actionBarSelector"
        const val key_player_actionBarTitle = "player_actionBarTitle"
        const val key_player_actionBarTop = "player_actionBarTop"
        const val key_player_actionBarSubtitle = "player_actionBarSubtitle"
        const val key_player_actionBarItems = "player_actionBarItems"
        const val key_player_background = "player_background"
        const val key_player_time = "player_time"
        const val key_player_progressBackground = "player_progressBackground"
        const val key_player_progress = "player_progress"
        const val key_player_placeholder = "player_placeholder"
        const val key_player_placeholderBackground = "player_placeholderBackground"
        const val key_player_button = "player_button"
        const val key_player_buttonActive = "player_buttonActive"

       /* const val key_Tajrish_line = "Tajrish_line"
        const val key_Farhangsara_line = "Farhangsara_line"
        const val key_Azadegan_line = "Azadegan_line"
        const val key_kolahdooz_line = "kolahdooz_line"
        const val key_FarhangsaraSubLine_line = "FarhangsaraSubLine_line"
        const val key_Abdolazim_line = "Abdolazim_line"
        const val key_Takhti_line = "Takhti_line"
        const val key_metro_background = "metro_background"*/

        init{
            dividerPaint.setStrokeWidth(1f)
            dividerPaint.setColor(getColor(key_divider))

            /*defaultColors.put(key_Tajrish_line, -0x1a9aab)
            defaultColors.put(key_Farhangsara_line, Color.BLUE)
            defaultColors.put(key_Azadegan_line, -0xab6323)
            defaultColors.put(key_kolahdooz_line, -0x5600)
            defaultColors.put(key_FarhangsaraSubLine_line, -0x8937b3)
            defaultColors.put(key_Abdolazim_line, -0x6634)
            defaultColors.put(key_Takhti_line, -0x66ff67)
            defaultColors.put(key_metro_background, -0xd0d0e)*/

            defaultColors.put(key_dialogBackground, -0x1)
            defaultColors.put(key_dialogBackgroundGray, -0xf0f10)
            defaultColors.put(key_dialogTextBlack, -0xdededf)
            defaultColors.put(key_dialogTextLink, -0xd9874a)
            defaultColors.put(key_dialogLinkSelection, 0x3362a9e3)
            defaultColors.put(key_dialogTextRed, -0x32a5a6)
            defaultColors.put(key_dialogTextBlue, -0xd07337)
            defaultColors.put(key_dialogTextBlue2, -0xc57331)
            defaultColors.put(key_dialogTextBlue3, -0xc13e07)
            defaultColors.put(key_dialogTextBlue4, -0xe65818)
            defaultColors.put(key_dialogTextGray, -0xcb743f)
            defaultColors.put(key_dialogTextGray2, -0x8a8a8b)
            defaultColors.put(key_dialogTextGray3, -0x666667)
            defaultColors.put(key_dialogTextGray4, -0x4c4c4d)
            defaultColors.put(key_dialogTextHint, -0x686869)
            defaultColors.put(key_dialogIcon, -0x757576)
            defaultColors.put(key_dialogGrayLine, -0x2d2d2e)
            defaultColors.put(key_dialogTopBackground, -0x904d1b)
            defaultColors.put(key_dialogInputField, -0x242425)
            defaultColors.put(key_dialogInputFieldActivated, -0xc85610)
            defaultColors.put(key_dialogCheckboxSquareBackground, -0xbc5f21)
            defaultColors.put(key_dialogCheckboxSquareCheck, -0x1)
            defaultColors.put(key_dialogCheckboxSquareUnchecked, -0x8c8c8d)
            defaultColors.put(key_dialogCheckboxSquareDisabled, -0x4f4f50)
            defaultColors.put(key_dialogRadioBackground, -0x4c4c4d)
            defaultColors.put(key_dialogRadioBackgroundChecked, -0xc85610)
            defaultColors.put(key_dialogProgressCircle, -0xad825d)
            defaultColors.put(key_dialogLineProgress, -0xad825d)
            defaultColors.put(key_dialogLineProgressBackground, -0x242425)
            defaultColors.put(key_dialogButton, -0xb66e34)
            defaultColors.put(key_dialogButtonSelector, 0x0f000000)
            defaultColors.put(key_dialogScrollGlow, -0xa0909)
            defaultColors.put(key_dialogRoundCheckBox, -0xc13e07)
            defaultColors.put(key_dialogRoundCheckBoxCheck, -0x1)
            defaultColors.put(key_dialogBadgeBackground, -0xc13e07)
            defaultColors.put(key_dialogBadgeText, -0x1)

            defaultColors.put(key_windowBackgroundWhite, -0x1)
            defaultColors.put(key_progressCircle, -0xad825d)
            defaultColors.put(key_windowBackgroundWhiteGrayIcon, -0x8c8c8d)
            defaultColors.put(key_windowBackgroundWhiteBlueText, -0xc47b40)
            defaultColors.put(key_windowBackgroundWhiteBlueText2, -0xcb743f)
            defaultColors.put(key_windowBackgroundWhiteBlueText3, -0xd9874a)
            defaultColors.put(key_windowBackgroundWhiteBlueText4, -0xb27c4d)
            defaultColors.put(key_windowBackgroundWhiteBlueText5, -0xb37136)
            defaultColors.put(key_windowBackgroundWhiteBlueText6, -0xc57331)
            defaultColors.put(key_windowBackgroundWhiteBlueText7, -0xc88552)
            defaultColors.put(key_windowBackgroundWhiteGreenText, -0xd968d4)
            defaultColors.put(key_windowBackgroundWhiteGreenText2, -0xc856e7)
            defaultColors.put(key_windowBackgroundWhiteRedText, -0x32a5a6)
            defaultColors.put(key_windowBackgroundWhiteRedText2, -0x24aeaf)
            defaultColors.put(key_windowBackgroundWhiteRedText3, -0x2db6b7)
            defaultColors.put(key_windowBackgroundWhiteRedText4, -0x30cfd0)
            defaultColors.put(key_windowBackgroundWhiteRedText5, -0x12c2c7)
            defaultColors.put(key_windowBackgroundWhiteRedText6, -0x999a)
            defaultColors.put(key_windowBackgroundWhiteGrayText, -0x575758)
            defaultColors.put(key_windowBackgroundWhiteGrayText2, -0x757576)
            defaultColors.put(key_windowBackgroundWhiteGrayText3, -0x666667)
            defaultColors.put(key_windowBackgroundWhiteGrayText4, -0x7f7f80)
            defaultColors.put(key_windowBackgroundWhiteGrayText5, -0x5c5c5d)
            defaultColors.put(key_windowBackgroundWhiteGrayText6, -0x8a8a8b)
            defaultColors.put(key_windowBackgroundWhiteGrayText7, -0x39393a)
            defaultColors.put(key_windowBackgroundWhiteGrayText8, -0x92928e)
            defaultColors.put(key_windowBackgroundWhiteGrayLine, -0x242425)
            defaultColors.put(key_windowBackgroundWhiteBlackText, -0xdededf)
            defaultColors.put(key_windowBackgroundWhiteHintText, -0x686869)
            defaultColors.put(key_windowBackgroundWhiteValueText, -0xd07337)
            defaultColors.put(key_windowBackgroundWhiteLinkText, -0xd9874a)
            defaultColors.put(key_windowBackgroundWhiteLinkSelection, 0x3362a9e3)
            defaultColors.put(key_windowBackgroundWhiteBlueHeader, -0xc16f31)
            defaultColors.put(key_windowBackgroundWhiteInputField, -0x242425)
            defaultColors.put(key_windowBackgroundWhiteInputFieldActivated, -0xc85610)
            defaultColors.put(key_switchThumb, -0x121213)
            defaultColors.put(key_switchTrack, -0x383839)
            defaultColors.put(key_switchThumbChecked, -0xba5411)
            defaultColors.put(key_switchTrackChecked, -0x5f2906)
            defaultColors.put(key_checkboxSquareBackground, -0xbc5f21)
            defaultColors.put(key_checkboxSquareCheck, -0x1)
            defaultColors.put(key_checkboxSquareUnchecked, -0x8c8c8d)
            defaultColors.put(key_checkboxSquareDisabled, -0x4f4f50)
            defaultColors.put(key_listSelector, 0x0f000000)
            defaultColors.put(key_radioBackground, -0x4c4c4d)
            defaultColors.put(key_radioBackgroundChecked, -0xc85610)
            defaultColors.put(key_windowBackgroundGray, -0xf0f10)
            defaultColors.put(key_windowBackgroundGrayShadow, -0x1000000)
            defaultColors.put(key_emptyListPlaceholder, -0x6a6a6b)
            defaultColors.put(key_divider, -0x262627)
            defaultColors.put(key_graySection, -0xd0d0e)
            defaultColors.put(key_contextProgressInner1, -0x40200a)
            defaultColors.put(key_contextProgressOuter1, -0xd4691e)
            defaultColors.put(key_contextProgressInner2, -0x40200a)
            defaultColors.put(key_contextProgressOuter2, -0x1)
            defaultColors.put(key_contextProgressInner3, -0x4c4c4d)
            defaultColors.put(key_contextProgressOuter3, -0x1)
            defaultColors.put(key_fastScrollActive, -0xad5c25)
            defaultColors.put(key_fastScrollInactive, -0x9c9c9d)
            defaultColors.put(key_fastScrollText, -0x1)

            defaultColors.put(key_avatar_text, -0x1)

            defaultColors.put(key_avatar_backgroundSaved, -0x994006)
            defaultColors.put(key_avatar_backgroundRed, -0x1a9aab)
            defaultColors.put(key_avatar_backgroundOrange, -0xd73b8)
            defaultColors.put(key_avatar_backgroundViolet, -0x717a12)
            defaultColors.put(key_avatar_backgroundGreen, -0x8937b3)
            defaultColors.put(key_avatar_backgroundCyan, -0xa0412b)
            defaultColors.put(key_avatar_backgroundBlue, -0xab6323)
            defaultColors.put(key_avatar_backgroundPink, -0xd8b66)
            defaultColors.put(key_avatar_backgroundGroupCreateSpanBlue, -0x402916)
            defaultColors.put(key_avatar_backgroundInProfileRed, -0x27909b)
            defaultColors.put(key_avatar_backgroundInProfileOrange, -0x9629f)
            defaultColors.put(key_avatar_backgroundInProfileViolet, -0x73862e)
            defaultColors.put(key_avatar_backgroundInProfileGreen, -0x984ca3)
            defaultColors.put(key_avatar_backgroundInProfileCyan, -0xa95d45)
            defaultColors.put(key_avatar_backgroundInProfileBlue, -0xaf7a4f)
            defaultColors.put(key_avatar_backgroundInProfilePink, -0xc805a)
            defaultColors.put(key_avatar_backgroundActionBarRed, -0x359faa)
            defaultColors.put(key_avatar_backgroundActionBarOrange, -0xe76bc)
            defaultColors.put(key_avatar_backgroundActionBarViolet, -0x82953c)
            defaultColors.put(key_avatar_backgroundActionBarGreen, -0xa95eb4)
            defaultColors.put(key_avatar_backgroundActionBarCyan, -0xbb6d54)
            defaultColors.put(key_avatar_backgroundActionBarBlue, -0xa67046)
            defaultColors.put(key_avatar_backgroundActionBarPink, -0xa67046)
            defaultColors.put(key_avatar_subtitleInProfileRed, -0x6343b)
            defaultColors.put(key_avatar_subtitleInProfileOrange, -0x22238)
            defaultColors.put(key_avatar_subtitleInProfileViolet, -0x323b13)
            defaultColors.put(key_avatar_subtitleInProfileGreen, -0x3f1246)
            defaultColors.put(key_avatar_subtitleInProfileCyan, -0x471d10)
            defaultColors.put(key_avatar_subtitleInProfileBlue, -0x281506)
            defaultColors.put(key_avatar_subtitleInProfilePink, -0x281506)
            defaultColors.put(key_avatar_nameInMessageRed, -0x35a9b0)
            defaultColors.put(key_avatar_nameInMessageOrange, -0x2784d7)
            defaultColors.put(key_avatar_nameInMessageViolet, -0xb16d34)
            defaultColors.put(key_avatar_nameInMessageGreen, -0xaf4dce)
            defaultColors.put(key_avatar_nameInMessageCyan, -0xbd4e58)
            defaultColors.put(key_avatar_nameInMessageBlue, -0xb16d34)
            defaultColors.put(key_avatar_nameInMessagePink, -0xb16d34)
            defaultColors.put(key_avatar_actionBarSelectorRed, -0x43b4bf)
            defaultColors.put(key_avatar_actionBarSelectorOrange, -0x198bd7)
            defaultColors.put(key_avatar_actionBarSelectorViolet, -0x8ca042)
            defaultColors.put(key_avatar_actionBarSelectorGreen, -0xb76ac3)
            defaultColors.put(key_avatar_actionBarSelectorCyan, -0xc67b63)
            defaultColors.put(key_avatar_actionBarSelectorBlue, -0xb67e53)
            defaultColors.put(key_avatar_actionBarSelectorPink, -0xb67e53)
            defaultColors.put(key_avatar_actionBarIconRed, -0x1)
            defaultColors.put(key_avatar_actionBarIconOrange, -0x1)
            defaultColors.put(key_avatar_actionBarIconViolet, -0x1)
            defaultColors.put(key_avatar_actionBarIconGreen, -0x1)
            defaultColors.put(key_avatar_actionBarIconCyan, -0x1)
            defaultColors.put(key_avatar_actionBarIconBlue, -0x1)
            defaultColors.put(key_avatar_actionBarIconPink, -0x1)

            defaultColors.put(key_actionBarDefault, -0xad825d)
            defaultColors.put(key_actionBarDefaultIcon, -0x1)
            defaultColors.put(key_actionBarActionModeDefault, -0x1)
            defaultColors.put(key_actionBarActionModeDefaultTop, -0x67000000)
            defaultColors.put(key_actionBarActionModeDefaultIcon, -0x8c8c8d)
            defaultColors.put(key_actionBarDefaultTitle, -0x1)
            defaultColors.put(key_actionBarDefaultSubtitle, -0x2a1709)
            defaultColors.put(key_actionBarDefaultSelector, -0xbf926c)
            defaultColors.put(key_actionBarWhiteSelector, 0x2f000000)
            defaultColors.put(key_actionBarDefaultSearch, -0x1)
            defaultColors.put(key_actionBarDefaultSearchPlaceholder, -0x77000001)
            defaultColors.put(key_actionBarDefaultSubmenuItem, -0xdededf)
            defaultColors.put(key_actionBarDefaultSubmenuBackground, -0x1)
            defaultColors.put(key_actionBarActionModeDefaultSelector, -0xf0f10)

            defaultColors.put(key_chats_unreadCounter, -0xb133a2)
            defaultColors.put(key_chats_unreadCounterMuted, -0x383839)
            defaultColors.put(key_chats_unreadCounterText, -0x1)
            defaultColors.put(key_chats_name, -0xdededf)
            defaultColors.put(key_chats_secretName, -0xff59f2)
            defaultColors.put(key_chats_secretIcon, -0xe64eda)
            defaultColors.put(key_chats_nameIcon, -0xdbdbdc)
            defaultColors.put(key_chats_pinnedIcon, -0x575758)
            defaultColors.put(key_chats_message, -0x707071)
            defaultColors.put(key_chats_draft, -0x22b4c7)
            defaultColors.put(key_chats_nameMessage, -0xb27c4d)
            defaultColors.put(key_chats_attachMessage, -0xb27c4d)
            defaultColors.put(key_chats_actionMessage, -0xb27c4d)
            defaultColors.put(key_chats_date, -0x666667)
            defaultColors.put(key_chats_pinnedOverlay, 0x08000000)
            defaultColors.put(key_chats_tabletSelectedOverlay, 0x0f000000)
            defaultColors.put(key_chats_sentCheck, -0xb955ca)
            defaultColors.put(key_chats_sentClock, -0x8a42a2)
            defaultColors.put(key_chats_sentError, -0x2aadae)
            defaultColors.put(key_chats_sentErrorIcon, -0x1)
            defaultColors.put(key_chats_verifiedBackground, -0xcc571a)
            defaultColors.put(key_chats_verifiedCheck, -0x1)
            defaultColors.put(key_chats_muteIcon, -0x575758)
            defaultColors.put(key_chats_menuBackground, -0x1)
            defaultColors.put(key_chats_menuItemText, -0xbbbbbc)
            defaultColors.put(key_chats_menuItemIcon, -0x8c8c8d)
            defaultColors.put(key_chats_menuName, -0x1)
            defaultColors.put(key_chats_menuPhone, -0x1)
            defaultColors.put(key_chats_menuPhoneCats, -0x3d1a01)
            defaultColors.put(key_chats_menuCloud, -0x1)
            defaultColors.put(key_chats_menuCloudBackgroundCats, -0xbd8457)
            defaultColors.put(key_chats_actionIcon, -0x1)
            defaultColors.put(key_chats_actionBackground, -0x955e32)
            defaultColors.put(key_chats_actionPressedBackground, -0xa86d3e)

            defaultColors.put(key_chat_lockIcon, -0x1)
            defaultColors.put(key_chat_muteIcon, -0x4e331d)
            defaultColors.put(key_chat_inBubble, -0x1)
            defaultColors.put(key_chat_inBubbleSelected, -0x1d0701)
            defaultColors.put(key_chat_inBubbleShadow, -0xe2c8ad)
            defaultColors.put(key_chat_outBubble, -0x100022)
            defaultColors.put(key_chat_outBubbleSelected, -0x2b0a44)
            defaultColors.put(key_chat_outBubbleShadow, -0xe18af4)
            defaultColors.put(key_chat_messageTextIn, -0x1000000)
            defaultColors.put(key_chat_messageTextOut, -0x1000000)
            defaultColors.put(key_chat_messageLinkIn, -0xd9874a)
            defaultColors.put(key_chat_messageLinkOut, -0xd9874a)
            defaultColors.put(key_chat_serviceText, -0x1)
            defaultColors.put(key_chat_serviceLink, -0x1)
            defaultColors.put(key_chat_serviceIcon, -0x1)
            defaultColors.put(key_chat_mediaTimeBackground, 0x66000000)
            defaultColors.put(key_chat_outSentCheck, -0xa24fb0)
            defaultColors.put(key_chat_outSentCheckSelected, -0xa24fb0)
            defaultColors.put(key_chat_outSentClock, -0x8a42a2)
            defaultColors.put(key_chat_outSentClockSelected, -0x8a42a2)
            defaultColors.put(key_chat_inSentClock, -0x5e554d)
            defaultColors.put(key_chat_inSentClockSelected, -0x6c4236)
            defaultColors.put(key_chat_mediaSentCheck, -0x1)
            defaultColors.put(key_chat_mediaSentClock, -0x1)
            defaultColors.put(key_chat_inViews, -0x5e554d)
            defaultColors.put(key_chat_inViewsSelected, -0x6c4236)
            defaultColors.put(key_chat_outViews, -0x914da9)
            defaultColors.put(key_chat_outViewsSelected, -0x914da9)
            defaultColors.put(key_chat_mediaViews, -0x1)
            defaultColors.put(key_chat_inMenu, -0x49423b)
            defaultColors.put(key_chat_inMenuSelected, -0x673e32)
            defaultColors.put(key_chat_outMenu, -0x6e3182)
            defaultColors.put(key_chat_outMenuSelected, -0x6e3182)
            defaultColors.put(key_chat_mediaMenu, -0x1)
            defaultColors.put(key_chat_outInstant, -0xaa54b1)
            defaultColors.put(key_chat_outInstantSelected, -0xb766bd)
            defaultColors.put(key_chat_inInstant, -0xc57331)
            defaultColors.put(key_chat_inInstantSelected, -0xcf864b)
            defaultColors.put(key_chat_sentError, -0x24cacb)
            defaultColors.put(key_chat_sentErrorIcon, -0x1)
            defaultColors.put(key_chat_selectedBackground, 0x6633b5e5)
            defaultColors.put(key_chat_previewDurationText, -0x1)
            defaultColors.put(key_chat_previewGameText, -0x1)
            defaultColors.put(key_chat_inPreviewInstantText, -0xc57331)
            defaultColors.put(key_chat_outPreviewInstantText, -0xaa54b1)
            defaultColors.put(key_chat_inPreviewInstantSelectedText, -0xcf864b)
            defaultColors.put(key_chat_outPreviewInstantSelectedText, -0xb766bd)
            defaultColors.put(key_chat_secretTimeText, -0x1b1d20)
            defaultColors.put(key_chat_stickerNameText, -0x1)
            defaultColors.put(key_chat_botButtonText, -0x1)
            defaultColors.put(key_chat_botProgress, -0x1)
            defaultColors.put(key_chat_inForwardedNameText, -0xc77939)
            defaultColors.put(key_chat_outForwardedNameText, -0xaa54b1)
            defaultColors.put(key_chat_inViaBotNameText, -0xc57331)
            defaultColors.put(key_chat_outViaBotNameText, -0xaa54b1)
            defaultColors.put(key_chat_stickerViaBotNameText, -0x1)
            defaultColors.put(key_chat_inReplyLine, -0xa66028)
            defaultColors.put(key_chat_outReplyLine, -0x914697)
            defaultColors.put(key_chat_stickerReplyLine, -0x1)
            defaultColors.put(key_chat_inReplyNameText, -0xc57331)
            defaultColors.put(key_chat_outReplyNameText, -0xaa54b1)
            defaultColors.put(key_chat_stickerReplyNameText, -0x1)
            defaultColors.put(key_chat_inReplyMessageText, -0x1000000)
            defaultColors.put(key_chat_outReplyMessageText, -0x1000000)
            defaultColors.put(key_chat_inReplyMediaMessageText, -0x5e554d)
            defaultColors.put(key_chat_outReplyMediaMessageText, -0x9a4fa5)
            defaultColors.put(key_chat_inReplyMediaMessageSelectedText, -0x764b3f)
            defaultColors.put(key_chat_outReplyMediaMessageSelectedText, -0x9a4fa5)
            defaultColors.put(key_chat_stickerReplyMessageText, -0x1)
            defaultColors.put(key_chat_inPreviewLine, -0x8f4b18)
            defaultColors.put(key_chat_outPreviewLine, -0x773685)
            defaultColors.put(key_chat_inSiteNameText, -0xc57331)
            defaultColors.put(key_chat_outSiteNameText, -0xaa54b1)
            defaultColors.put(key_chat_inContactNameText, -0xb1652c)
            defaultColors.put(key_chat_outContactNameText, -0xaa54b1)
            defaultColors.put(key_chat_inContactPhoneText, -0xd0cbc8)
            defaultColors.put(key_chat_outContactPhoneText, -0xcabdcc)
            defaultColors.put(key_chat_mediaProgress, -0x1)
            defaultColors.put(key_chat_inAudioProgress, -0x1)
            defaultColors.put(key_chat_outAudioProgress, -0x100022)
            defaultColors.put(key_chat_inAudioSelectedProgress, -0x1d0701)
            defaultColors.put(key_chat_outAudioSelectedProgress, -0x2b0a44)
            defaultColors.put(key_chat_mediaTimeText, -0x1)
            defaultColors.put(key_chat_inTimeText, -0x5e554d)
            defaultColors.put(key_chat_outTimeText, -0x8f4ea4)
            defaultColors.put(key_chat_adminText, -0x3f3935)
            defaultColors.put(key_chat_adminSelectedText, -0x764b3f)
            defaultColors.put(key_chat_inTimeSelectedText, -0x764b3f)
            defaultColors.put(key_chat_outTimeSelectedText, -0x8f4ea4)
            defaultColors.put(key_chat_inAudioPerfomerText, -0xd0cbc8)
            defaultColors.put(key_chat_outAudioPerfomerText, -0xcabdcc)
            defaultColors.put(key_chat_inAudioTitleText, -0xb1652c)
            defaultColors.put(key_chat_outAudioTitleText, -0xaa54b1)
            defaultColors.put(key_chat_inAudioDurationText, -0x5e554d)
            defaultColors.put(key_chat_outAudioDurationText, -0x9a4fa5)
            defaultColors.put(key_chat_inAudioDurationSelectedText, -0x764b3f)
            defaultColors.put(key_chat_outAudioDurationSelectedText, -0x9a4fa5)
            defaultColors.put(key_chat_inAudioSeekbar, -0x1b1510)
            defaultColors.put(key_chat_outAudioSeekbar, -0x441c54)
            defaultColors.put(key_chat_inAudioSeekbarSelected, -0x432118)
            defaultColors.put(key_chat_outAudioSeekbarSelected, -0x56226a)
            defaultColors.put(key_chat_inAudioSeekbarFill, -0x8d4a18)
            defaultColors.put(key_chat_outAudioSeekbarFill, -0x873d8e)
            defaultColors.put(key_chat_inVoiceSeekbar, -0x211a15)
            defaultColors.put(key_chat_outVoiceSeekbar, -0x441c54)
            defaultColors.put(key_chat_inVoiceSeekbarSelected, -0x432118)
            defaultColors.put(key_chat_outVoiceSeekbarSelected, -0x56226a)
            defaultColors.put(key_chat_inVoiceSeekbarFill, -0x8d4a18)
            defaultColors.put(key_chat_outVoiceSeekbarFill, -0x873d8e)
            defaultColors.put(key_chat_inFileProgress, -0x140f0b)
            defaultColors.put(key_chat_outFileProgress, -0x250a3d)
            defaultColors.put(key_chat_inFileProgressSelected, -0x34150a)
            defaultColors.put(key_chat_outFileProgressSelected, -0x3a1359)
            defaultColors.put(key_chat_inFileNameText, -0xb1652c)
            defaultColors.put(key_chat_outFileNameText, -0xaa54b1)
            defaultColors.put(key_chat_inFileInfoText, -0x5e554d)
            defaultColors.put(key_chat_outFileInfoText, -0x9a4fa5)
            defaultColors.put(key_chat_inFileInfoSelectedText, -0x764b3f)
            defaultColors.put(key_chat_outFileInfoSelectedText, -0x9a4fa5)
            defaultColors.put(key_chat_inFileBackground, -0x140f0b)
            defaultColors.put(key_chat_outFileBackground, -0x250a3d)
            defaultColors.put(key_chat_inFileBackgroundSelected, -0x34150a)
            defaultColors.put(key_chat_outFileBackgroundSelected, -0x3a1359)
            defaultColors.put(key_chat_inVenueNameText, -0xb1652c)
            defaultColors.put(key_chat_outVenueNameText, -0xaa54b1)
            defaultColors.put(key_chat_inVenueInfoText, -0x5e554d)
            defaultColors.put(key_chat_outVenueInfoText, -0x9a4fa5)
            defaultColors.put(key_chat_inVenueInfoSelectedText, -0x764b3f)
            defaultColors.put(key_chat_outVenueInfoSelectedText, -0x9a4fa5)
            defaultColors.put(key_chat_mediaInfoText, -0x1)
            defaultColors.put(key_chat_linkSelectBackground, 0x3362a9e3)
            defaultColors.put(key_chat_textSelectBackground, 0x6662a9e3)
            defaultColors.put(key_chat_emojiPanelBackground, -0xa0909)
            defaultColors.put(key_chat_emojiPanelShadowLine, -0x1d1a19)
            defaultColors.put(key_chat_emojiPanelEmptyText, -0x777778)
            defaultColors.put(key_chat_emojiPanelIcon, -0x575758)
            defaultColors.put(key_chat_emojiPanelIconSelected, -0xd4691e)
            defaultColors.put(key_chat_emojiPanelStickerPackSelector, -0x1d1a19)
            defaultColors.put(key_chat_emojiPanelIconSelector, -0xd4691e)
            defaultColors.put(key_chat_emojiPanelBackspace, -0x575758)
            defaultColors.put(key_chat_emojiPanelMasksIcon, -0x1)
            defaultColors.put(key_chat_emojiPanelMasksIconSelected, -0x9d4018)
            defaultColors.put(key_chat_emojiPanelTrendingTitle, -0xdededf)
            defaultColors.put(key_chat_emojiPanelStickerSetName, -0x7c736a)
            defaultColors.put(key_chat_emojiPanelStickerSetNameIcon, -0x4e4944)
            defaultColors.put(key_chat_emojiPanelTrendingDescription, -0x757576)
            defaultColors.put(key_chat_botKeyboardButtonText, -0xc9b8b1)
            defaultColors.put(key_chat_botKeyboardButtonBackground, -0x1b1817)
            defaultColors.put(key_chat_botKeyboardButtonBackgroundPressed, -0x332e2c)
            defaultColors.put(key_chat_unreadMessagesStartArrowIcon, -0x5d4a39)
            defaultColors.put(key_chat_unreadMessagesStartText, -0xa96a34)
            defaultColors.put(key_chat_unreadMessagesStartBackground, -0x1)
            defaultColors.put(key_chat_editDoneIcon, -0xae420d)
            defaultColors.put(key_chat_inFileIcon, -0x5d4a39)
            defaultColors.put(key_chat_inFileSelectedIcon, -0x78493b)
            defaultColors.put(key_chat_outFileIcon, -0x7a4088)
            defaultColors.put(key_chat_outFileSelectedIcon, -0x7a4088)
            defaultColors.put(key_chat_inLocationBackground, -0x140f0b)
            defaultColors.put(key_chat_inLocationIcon, -0x5d4a39)
            defaultColors.put(key_chat_outLocationBackground, -0x250a3d)
            defaultColors.put(key_chat_outLocationIcon, -0x784088)
            defaultColors.put(key_chat_inContactBackground, -0x8d4a18)
            defaultColors.put(key_chat_inContactIcon, -0x1)
            defaultColors.put(key_chat_outContactBackground, -0x873d8e)
            defaultColors.put(key_chat_outContactIcon, -0x100022)
            defaultColors.put(key_chat_outBroadcast, -0xb955ca)
            defaultColors.put(key_chat_mediaBroadcast, -0x1)
            defaultColors.put(key_chat_searchPanelIcons, -0xa25a24)
            defaultColors.put(key_chat_searchPanelText, -0xb1652c)
            defaultColors.put(key_chat_secretChatStatusText, -0x808081)
            defaultColors.put(key_chat_fieldOverlayText, -0xc57331)
            defaultColors.put(key_chat_stickersHintPanel, -0x1)
            defaultColors.put(key_chat_replyPanelIcons, -0xa8571a)
            defaultColors.put(key_chat_replyPanelClose, -0x575758)
            defaultColors.put(key_chat_replyPanelName, -0xc57331)
            defaultColors.put(key_chat_replyPanelMessage, -0xddddde)
            defaultColors.put(key_chat_replyPanelLine, -0x171718)
            defaultColors.put(key_chat_messagePanelBackground, -0x1)
            defaultColors.put(key_chat_messagePanelText, -0x1000000)
            defaultColors.put(key_chat_messagePanelHint, -0x4d4d4e)
            defaultColors.put(key_chat_messagePanelShadow, -0x1000000)
            defaultColors.put(key_chat_messagePanelIcons, -0x575758)
            defaultColors.put(key_chat_recordedVoicePlayPause, -0x1)
            defaultColors.put(key_chat_recordedVoicePlayPausePressed, -0x261505)
            defaultColors.put(key_chat_recordedVoiceDot, -0x25a9b3)
            defaultColors.put(key_chat_recordedVoiceBackground, -0xaa611d)
            defaultColors.put(key_chat_recordedVoiceProgress, -0x5d3108)
            defaultColors.put(key_chat_recordedVoiceProgressInner, -0x1)
            defaultColors.put(key_chat_recordVoiceCancel, -0x666667)
            defaultColors.put(key_chat_messagePanelSend, -0x9d4f15)
            defaultColors.put(key_chat_messagePanelVoiceLock, -0x5b5b5c)
            defaultColors.put(key_chat_messagePanelVoiceLockBackground, -0x1)
            defaultColors.put(key_chat_messagePanelVoiceLockShadow, -0x1000000)
            defaultColors.put(key_chat_recordTime, -0xb2b3b5)
            defaultColors.put(key_chat_emojiPanelNewTrending, -0xb25916)
            defaultColors.put(key_chat_gifSaveHintText, -0x1)
            defaultColors.put(key_chat_gifSaveHintBackground, -0x33eeeeef)
            defaultColors.put(key_chat_goDownButton, -0x1)
            defaultColors.put(key_chat_goDownButtonShadow, -0x1000000)
            defaultColors.put(key_chat_goDownButtonIcon, -0x575758)
            defaultColors.put(key_chat_goDownButtonCounter, -0x1)
            defaultColors.put(key_chat_goDownButtonCounterBackground, -0xb25d18)
            defaultColors.put(key_chat_messagePanelCancelInlineBot, -0x525253)
            defaultColors.put(key_chat_messagePanelVoicePressed, -0x1)
            defaultColors.put(key_chat_messagePanelVoiceBackground, -0xa86a34)
            defaultColors.put(key_chat_messagePanelVoiceShadow, 0x0d000000)
            defaultColors.put(key_chat_messagePanelVoiceDelete, -0x8c8c8d)
            defaultColors.put(key_chat_messagePanelVoiceDuration, -0x1)
            defaultColors.put(key_chat_inlineResultIcon, -0xa86a34)
            defaultColors.put(key_chat_topPanelBackground, -0x1)
            defaultColors.put(key_chat_topPanelClose, -0x575758)
            defaultColors.put(key_chat_topPanelLine, -0x93602e)
            defaultColors.put(key_chat_topPanelTitle, -0xc57331)
            defaultColors.put(key_chat_topPanelMessage, -0x666667)
            defaultColors.put(key_chat_reportSpam, -0x30a6a9)
            defaultColors.put(key_chat_addContact, -0xb57d4b)
            defaultColors.put(key_chat_inLoader, -0x8d4a18)
            defaultColors.put(key_chat_inLoaderSelected, -0x9a5420)
            defaultColors.put(key_chat_outLoader, -0x873d8e)
            defaultColors.put(key_chat_outLoaderSelected, -0x954a9c)
            defaultColors.put(key_chat_inLoaderPhoto, -0x5d4738)
            defaultColors.put(key_chat_inLoaderPhotoSelected, -0x5d4a39)
            defaultColors.put(key_chat_inLoaderPhotoIcon, -0x30304)
            defaultColors.put(key_chat_inLoaderPhotoIconSelected, -0x140f0b)
            defaultColors.put(key_chat_outLoaderPhoto, -0x7a4088)
            defaultColors.put(key_chat_outLoaderPhotoSelected, -0x824790)
            defaultColors.put(key_chat_outLoaderPhotoIcon, -0x250a3d)
            defaultColors.put(key_chat_outLoaderPhotoIconSelected, -0x3f175c)
            defaultColors.put(key_chat_mediaLoaderPhoto, 0x66000000)
            defaultColors.put(key_chat_mediaLoaderPhotoSelected, 0x7f000000)
            defaultColors.put(key_chat_mediaLoaderPhotoIcon, -0x1)
            defaultColors.put(key_chat_mediaLoaderPhotoIconSelected, -0x262627)
            defaultColors.put(key_chat_secretTimerBackground, -0x33c19b72)
            defaultColors.put(key_chat_secretTimerText, -0x1)

            defaultColors.put(key_profile_creatorIcon, -0xb5682a)
            defaultColors.put(key_profile_adminIcon, -0x7a7a7b)
            defaultColors.put(key_profile_actionIcon, -0x8c8c8d)
            defaultColors.put(key_profile_actionBackground, -0x1)
            defaultColors.put(key_profile_actionPressedBackground, -0xd0d0e)
            defaultColors.put(key_profile_verifiedBackground, -0x4d2908)
            defaultColors.put(key_profile_verifiedCheck, -0xb67c48)
            defaultColors.put(key_profile_title, -0x1)

            defaultColors.put(key_player_actionBar, -0x1)
            defaultColors.put(key_player_actionBarSelector, 0x2f000000)
            defaultColors.put(key_player_actionBarTitle, -0xd0cbc8)
            defaultColors.put(key_player_actionBarTop, -0x67000000)
            defaultColors.put(key_player_actionBarSubtitle, -0x757576)
            defaultColors.put(key_player_actionBarItems, -0x757576)
            defaultColors.put(key_player_background, -0x1)
            defaultColors.put(key_player_time, -0x736d6a)
            defaultColors.put(key_player_progressBackground, 0x19000000)
            defaultColors.put(key_player_progress, -0xdc5011)
            defaultColors.put(key_player_placeholder, -0x575758)
            defaultColors.put(key_player_placeholderBackground, -0xf0f10)
            defaultColors.put(key_player_button, -0xcccccd)
            defaultColors.put(key_player_buttonActive, -0xb35716)

            defaultColors.put(key_files_folderIcon, -0x666667)
            defaultColors.put(key_files_folderIconBackground, -0xf0f10)
            defaultColors.put(key_files_iconText, -0x1)

            defaultColors.put(key_sessions_devicesImage, -0x69696a)

            defaultColors.put(key_location_markerX, -0x7f7f80)
            defaultColors.put(key_location_sendLocationBackground, -0x925f2c)
            defaultColors.put(key_location_sendLiveLocationBackground, -0x9b9c)
            defaultColors.put(key_location_sendLocationIcon, -0x1)
            defaultColors.put(key_location_liveLocationProgress, -0xca601b)
            defaultColors.put(key_location_placeLocationBackground, -0xb35716)
            defaultColors.put(key_dialog_liveLocationProgress, -0xca601b)

            defaultColors.put(key_calls_callReceivedGreenIcon, -0xff37ad)
            defaultColors.put(key_calls_callReceivedRedIcon, -0xb7b8)

            defaultColors.put(key_featuredStickers_addedIcon, -0xaf5715)
            defaultColors.put(key_featuredStickers_buttonProgress, -0x1)
            defaultColors.put(key_featuredStickers_addButton, -0xaf5715)
            defaultColors.put(key_featuredStickers_addButtonPressed, -0xbc6422)
            defaultColors.put(key_featuredStickers_delButton, -0x26a8a9)
            defaultColors.put(key_featuredStickers_delButtonPressed, -0x39b6b7)
            defaultColors.put(key_featuredStickers_buttonText, -0x1)
            defaultColors.put(key_featuredStickers_unread, -0xb25916)

            defaultColors.put(key_inappPlayerPerformer, -0xd0cbc8)
            defaultColors.put(key_inappPlayerTitle, -0xd0cbc8)
            defaultColors.put(key_inappPlayerBackground, -0x1)
            defaultColors.put(key_inappPlayerPlayPause, -0x9d4f15)
            defaultColors.put(key_inappPlayerClose, -0x575758)

            defaultColors.put(key_returnToCallBackground, -0xbb5e1d)
            defaultColors.put(key_returnToCallText, -0x1)

            defaultColors.put(key_sharedMedia_startStopLoadIcon, -0xc95d12)
            defaultColors.put(key_sharedMedia_linkPlaceholder, -0xf0f10)
            defaultColors.put(key_sharedMedia_linkPlaceholderText, -0x1)
            defaultColors.put(key_checkbox, -0xa13dbb)
            defaultColors.put(key_checkboxCheck, -0x1)

            defaultColors.put(key_stickers_menu, -0x49423b)
            defaultColors.put(key_stickers_menuSelector, 0x2f000000)

            defaultColors.put(key_changephoneinfo_image, -0x575758)

            defaultColors.put(key_groupcreate_hintText, -0x5e554d)
            defaultColors.put(key_groupcreate_cursor, -0xad5c25)
            defaultColors.put(key_groupcreate_sectionShadow, -0x1000000)
            defaultColors.put(key_groupcreate_sectionText, -0x837d78)
            defaultColors.put(key_groupcreate_onlineText, -0xbf6d33)
            defaultColors.put(key_groupcreate_offlineText, -0x7c736a)
            defaultColors.put(key_groupcreate_checkbox, -0xa13dbb)
            defaultColors.put(key_groupcreate_checkboxCheck, -0x1)
            defaultColors.put(key_groupcreate_spanText, -0xdededf)
            defaultColors.put(key_groupcreate_spanBackground, -0xd0d0e)

            defaultColors.put(key_contacts_inviteBackground, -0xaa419f)
            defaultColors.put(key_contacts_inviteText, -0x1)

            defaultColors.put(key_login_progressInner, -0x1e150e)
            defaultColors.put(key_login_progressOuter, -0x9d5f30)

            defaultColors.put(key_musicPicker_checkbox, -0xd64909)
            defaultColors.put(key_musicPicker_checkboxCheck, -0x1)
            defaultColors.put(key_musicPicker_buttonBackground, -0xa35016)
            defaultColors.put(key_musicPicker_buttonIcon, -0x1)
            defaultColors.put(key_picker_enabledButton, -0xe65818)
            defaultColors.put(key_picker_disabledButton, -0x666667)
            defaultColors.put(key_picker_badge, -0xd64909)
            defaultColors.put(key_picker_badgeText, -0x1)

            defaultColors.put(key_chat_botSwitchToInlineText, -0xbc6e34)

            defaultColors.put(key_calls_ratingStar, -0x80000000)
            defaultColors.put(key_calls_ratingStarSelected, -0xb5682a)

            defaultColors[key_Tajrish_line] = -0x1a9aab
            defaultColors[key_Farhangsara_line] = Color.BLUE
            defaultColors[key_Azadegan_line] = -0xab6323
            defaultColors[key_kolahdooz_line] = -0x5600
            defaultColors[key_FarhangsaraSubLine_line] = -0x8937b3
            defaultColors[key_Abdolazim_line] = -0x6634
            defaultColors[key_Takhti_line] = -0x66ff67
            defaultColors[key_metro_background] = -0xd0d0e
            defaultColors[key_contacts_inviteBackground] = -0xaa419f
            defaultColors[key_avatar_backgroundGreen] = -0x8937b3
            defaultColors[key_dialogRoundCheckBox] = -0xc13e07
            defaultColors[key_dialogRoundCheckBoxCheck] = -0x1
            defaultColors[key_contacts_inviteText] = -0x1
            defaultColors[key_chat_messagePanelVoicePressed] = -0x1
            defaultColors[key_windowBackgroundWhiteHintText] = -0x686869


        }
        fun getColor(key: String): Int {
            return getColor(key, null)
        }

        fun getColor(key: String, isDefault: BooleanArray?): Int {
            var color = currentColors!![key]
            if (color == null) {
                val fallbackKey = fallbackKeys[key]
                if (fallbackKey != null) {
                    color = currentColors[key]
                }
                if (color == null) {
                    if (isDefault != null) {
                        isDefault[0] = true
                    }
                    return getDefaultColor(key)
                }
            }
            return color
        }
        fun getDefaultColor(key: String): Int {
            return defaultColors[key]
                    ?: return if (key == key_chats_menuTopShadow) {
                        0
                    } else -0x10000
        }

        fun getSelectorDrawable(whiteBackground: Boolean): Drawable? {
            return if (whiteBackground) {
                if (Build.VERSION.SDK_INT >= 21) {
                    val maskDrawable: Drawable = ColorDrawable(-0x1)
                    val colorStateList = ColorStateList(arrayOf(StateSet.WILD_CARD), intArrayOf(getColor(key_listSelector)))
                    RippleDrawable(colorStateList, ColorDrawable(getColor(key_windowBackgroundWhite)), maskDrawable)
                } else {
                    val color: Int = getColor(key_listSelector)
                    val stateListDrawable = StateListDrawable()
                    stateListDrawable.addState(intArrayOf(R.attr.state_pressed), ColorDrawable(color))
                    stateListDrawable.addState(intArrayOf(R.attr.state_selected), ColorDrawable(color))
                    stateListDrawable.addState(StateSet.WILD_CARD, ColorDrawable(getColor(key_windowBackgroundWhite)))
                    stateListDrawable
                }
            } else {
                createSelectorDrawable(getColor(key_listSelector), 2)
            }
        }

        fun createSelectorDrawable(color: Int): Drawable? {
            return createSelectorDrawable(color, 1)
        }

        fun createSimpleSelectorCircleDrawable(size: Int, defaultColor: Int, pressedColor: Int): Drawable? {
            val ovalShape = OvalShape()
            ovalShape.resize(size.toFloat(), size.toFloat())
            val defaultDrawable = ShapeDrawable(ovalShape)
            defaultDrawable.paint.color = defaultColor
            val pressedDrawable = ShapeDrawable(ovalShape)
            return if (Build.VERSION.SDK_INT >= 21) {
                pressedDrawable.paint.color = -0x1
                val colorStateList = ColorStateList(arrayOf(StateSet.WILD_CARD), intArrayOf(pressedColor))
                RippleDrawable(colorStateList, defaultDrawable, pressedDrawable)
            } else {
                pressedDrawable.paint.color = pressedColor
                val stateListDrawable = StateListDrawable()
                stateListDrawable.addState(intArrayOf(R.attr.state_pressed), pressedDrawable)
                stateListDrawable.addState(intArrayOf(R.attr.state_focused), pressedDrawable)
                stateListDrawable.addState(StateSet.WILD_CARD, defaultDrawable)
                stateListDrawable
            }
        }
        fun createSelectorDrawable(color: Int, maskType: Int): Drawable? {
            var drawable: Drawable
            return if (Build.VERSION.SDK_INT >= 21) {
                var maskDrawable: Drawable? = null
                if (maskType == 1) {
                    maskPaint.setColor(-0x1)
                    maskDrawable = object : Drawable() {
                        override fun draw(canvas: Canvas) {
                            val bounds = bounds
                            canvas.drawCircle(bounds.centerX().toFloat(), bounds.centerY().toFloat(), AndroidUtilities.dp(18f).toFloat(), maskPaint)
                        }

                        override fun setAlpha(alpha: Int) {}
                        override fun setColorFilter(colorFilter: ColorFilter) {}
                        override fun getOpacity(): Int {
                            return PixelFormat.UNKNOWN
                        }
                    }
                } else if (maskType == 2) {
                    maskDrawable = ColorDrawable(-0x1)
                }
                val colorStateList = ColorStateList(arrayOf(StateSet.WILD_CARD), intArrayOf(color))
                RippleDrawable(colorStateList, null, maskDrawable)
            } else {
                val stateListDrawable = StateListDrawable()
                stateListDrawable.addState(intArrayOf(R.attr.state_pressed), ColorDrawable(color))
                stateListDrawable.addState(intArrayOf(R.attr.state_selected), ColorDrawable(color))
                stateListDrawable.addState(StateSet.WILD_CARD, ColorDrawable(0x00000000))
                stateListDrawable
            }
        }
    }
}