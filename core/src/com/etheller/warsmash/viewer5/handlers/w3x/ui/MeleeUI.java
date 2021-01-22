package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.etheller.warsmash.datasources.DataSource;
import com.etheller.warsmash.parsers.fdf.GameUI;
import com.etheller.warsmash.parsers.fdf.datamodel.AnchorDefinition;
import com.etheller.warsmash.parsers.fdf.datamodel.FramePoint;
import com.etheller.warsmash.parsers.fdf.datamodel.TextJustify;
import com.etheller.warsmash.parsers.fdf.datamodel.Vector4Definition;
import com.etheller.warsmash.parsers.fdf.frames.FilterModeTextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.SetPoint;
import com.etheller.warsmash.parsers.fdf.frames.SimpleFrame;
import com.etheller.warsmash.parsers.fdf.frames.SimpleStatusBarFrame;
import com.etheller.warsmash.parsers.fdf.frames.SpriteFrame;
import com.etheller.warsmash.parsers.fdf.frames.StringFrame;
import com.etheller.warsmash.parsers.fdf.frames.TextureFrame;
import com.etheller.warsmash.parsers.fdf.frames.UIFrame;
import com.etheller.warsmash.parsers.jass.Jass2.RootFrameListener;
import com.etheller.warsmash.parsers.mdlx.Layer.FilterMode;
import com.etheller.warsmash.units.Element;
import com.etheller.warsmash.units.manager.MutableObjectData;
import com.etheller.warsmash.util.FastNumberFormat;
import com.etheller.warsmash.util.ImageUtils;
import com.etheller.warsmash.util.RenderMathUtils;
import com.etheller.warsmash.util.War3ID;
import com.etheller.warsmash.util.WarsmashConstants;
import com.etheller.warsmash.viewer5.Scene;
import com.etheller.warsmash.viewer5.ViewerTextureRenderable;
import com.etheller.warsmash.viewer5.handlers.mdx.Attachment;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxComplexInstance;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxModel;
import com.etheller.warsmash.viewer5.handlers.mdx.MdxNode;
import com.etheller.warsmash.viewer5.handlers.mdx.ReplaceableIds;
import com.etheller.warsmash.viewer5.handlers.mdx.SequenceLoopMode;
import com.etheller.warsmash.viewer5.handlers.tga.TgaFile;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.PrimaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.AnimationTokens.SecondaryTag;
import com.etheller.warsmash.viewer5.handlers.w3x.SequenceUtils;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel;
import com.etheller.warsmash.viewer5.handlers.w3x.SplatModel.SplatMover;
import com.etheller.warsmash.viewer5.handlers.w3x.UnitSound;
import com.etheller.warsmash.viewer5.handlers.w3x.War3MapViewer;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.CameraPreset;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.CameraRates;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.GameCameraManager;
import com.etheller.warsmash.viewer5.handlers.w3x.camera.PortraitCameraManager;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid.PathingFlags;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.AbilityDataUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.ability.IconUI;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.commandbuttons.CommandButtonListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CDestructable;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CItem;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CPlayerStateListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit.QueueItemType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitClassification;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitFilterFunction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitStateListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnitType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CWidget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityGeneric;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityMove;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityView;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.CAbilityVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.AbstractCAbilityBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityBuildInProgress;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityHumanBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNagaBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNeutralBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityNightElfBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityOrcBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.build.CAbilityUndeadBuild;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.combat.CAbilityColdArrows;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericNoIconAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.generic.GenericSingleIconActiveAbility;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityQueue;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.queue.CAbilityRally;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityPointTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTargetVisitor;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CAttackType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CDefenseType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.CodeKeyType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttack;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.combat.attacks.CUnitAttackMissileSplash;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.COrder;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.orders.OrderIds;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.pathing.CBuildingPathingType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayerUnitOrderListener;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CRace;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.AbilityActivationErrorHandler;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.BooleanAbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.CWidgetAbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.MeleeUIAbilityActivationReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.PointAbilityTargetCheckReceiver;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.ClickableActionFrame;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandCardCommandListener;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.CommandErrorListener;
import com.etheller.warsmash.viewer5.handlers.w3x.ui.command.QueueIconListener;

public class MeleeUI implements CUnitStateListener, CommandButtonListener, CommandCardCommandListener,
		QueueIconListener, CommandErrorListener, CPlayerStateListener {
	private static final long WORLD_FRAME_MESSAGE_FADEOUT_MILLIS = TimeUnit.SECONDS.toMillis(9);
	private static final long WORLD_FRAME_MESSAGE_EXPIRE_MILLIS = TimeUnit.SECONDS.toMillis(10);
	private static final long WORLD_FRAME_MESSAGE_FADE_DURATION = WORLD_FRAME_MESSAGE_EXPIRE_MILLIS
			- WORLD_FRAME_MESSAGE_FADEOUT_MILLIS;
	private static final String BUILDING_PATHING_PREVIEW_KEY = "buildingPathingPreview";
	public static final float DEFAULT_COMMAND_CARD_ICON_WIDTH = 0.039f;
	public static final float DEFAULT_COMMAND_CARD_ICON_PRESSED_WIDTH = 0.037f;
	private static final int COMMAND_CARD_WIDTH = 4;
	private static final int COMMAND_CARD_HEIGHT = 3;

	private static final Vector2 screenCoordsVector = new Vector2();
	private static final Vector3 clickLocationTemp = new Vector3();
	private static final AbilityPointTarget clickLocationTemp2 = new AbilityPointTarget();
	private final DataSource dataSource;
	private final ExtendViewport uiViewport;
	private final FreeTypeFontGenerator fontGenerator;
	private final Scene uiScene;
	private final Scene portraitScene;
	private final GameCameraManager cameraManager;
	private final War3MapViewer war3MapViewer;
	private final RootFrameListener rootFrameListener;
	private GameUI rootFrame;
	private UIFrame consoleUI;
	private UIFrame resourceBar;
	private StringFrame resourceBarGoldText;
	private StringFrame resourceBarLumberText;
	private StringFrame resourceBarSupplyText;
	private StringFrame resourceBarUpkeepText;
	private SpriteFrame timeIndicator;
	private UIFrame unitPortrait;
	private StringFrame unitLifeText;
	private StringFrame unitManaText;
	private Portrait portrait;
	private final Rectangle tempRect = new Rectangle();
	private final Vector2 projectionTemp1 = new Vector2();
	private final Vector2 projectionTemp2 = new Vector2();

	// tooltip
	private UIFrame tooltipFrame;
	private StringFrame tooltipText;

	private UIFrame simpleInfoPanelUnitDetail;
	private StringFrame simpleNameValue;
	private StringFrame simpleClassValue;
	private StringFrame simpleBuildingActionLabel;
	private SimpleStatusBarFrame simpleBuildTimeIndicator;

	private UIFrame simpleInfoPanelBuildingDetail;
	private StringFrame simpleBuildingNameValue;
	private StringFrame simpleBuildingDescriptionValue;
	private StringFrame simpleBuildingBuildingActionLabel;
	private SimpleStatusBarFrame simpleBuildingBuildTimeIndicator;
	private final QueueIcon[] queueIconFrames = new QueueIcon[WarsmashConstants.BUILD_QUEUE_SIZE];
	private QueueIcon selectWorkerInsideFrame;

	private UIFrame attack1Icon;
	private TextureFrame attack1IconBackdrop;
	private StringFrame attack1InfoPanelIconValue;
	private StringFrame attack1InfoPanelIconLevel;
	private UIFrame attack2Icon;
	private TextureFrame attack2IconBackdrop;
	private StringFrame attack2InfoPanelIconValue;
	private StringFrame attack2InfoPanelIconLevel;
	private UIFrame armorIcon;
	private TextureFrame armorIconBackdrop;
	private StringFrame armorInfoPanelIconValue;
	private StringFrame armorInfoPanelIconLevel;
	private InfoPanelIconBackdrops damageBackdrops;
	private InfoPanelIconBackdrops defenseBackdrops;

	private final CommandCardIcon[][] commandCard = new CommandCardIcon[COMMAND_CARD_HEIGHT][COMMAND_CARD_WIDTH];

	private RenderUnit selectedUnit;
	private final List<Integer> subMenuOrderIdStack = new ArrayList<>();

	// TODO remove this & replace with FDF
	private final Texture activeButtonTexture;
	private UIFrame inventoryCover;
	private SpriteFrame cursorFrame;
	private MeleeUIMinimap meleeUIMinimap;
	private final CPlayerUnitOrderListener unitOrderListener;
	private StringFrame errorMessageFrame;
	private long lastErrorMessageExpireTime;
	private long lastErrorMessageFadeTime;

	private CAbilityView activeCommand;
	private int activeCommandOrderId;
	private RenderUnit activeCommandUnit;
	private MdxComplexInstance cursorModelInstance = null;
	private MdxComplexInstance rallyPointInstance = null;
	private BufferedImage cursorModelPathing;
	private Pixmap cursorModelUnderneathPathingRedGreenPixmap;
	private Texture cursorModelUnderneathPathingRedGreenPixmapTexture;
	private PixmapTextureData cursorModelUnderneathPathingRedGreenPixmapTextureData;
	private SplatModel cursorModelUnderneathPathingRedGreenSplatModel;
	private CUnitType cursorBuildingUnitType;
	private SplatMover placementCursor = null;
	private final CursorTargetSetupVisitor cursorTargetSetupVisitor;

	private int selectedSoundCount = 0;
	private final ActiveCommandUnitTargetFilter activeCommandUnitTargetFilter;

	// TODO these corrections are used for old hardcoded UI stuff, we should
	// probably remove them later
	private final float widthRatioCorrection;
	private final float heightRatioCorrection;
	private ClickableActionFrame mouseDownUIFrame;
	private ClickableActionFrame mouseOverUIFrame;
	private UIFrame smashSimpleInfoPanel;
	private SimpleFrame smashAttack1IconWrapper;
	private SimpleFrame smashAttack2IconWrapper;
	private SimpleFrame smashArmorIconWrapper;
	private final RallyPositioningVisitor rallyPositioningVisitor;
	private final CPlayer localPlayer;
	private MeleeUIAbilityActivationReceiver meleeUIAbilityActivationReceiver;
	private MdxModel waypointModel;
	private final List<MdxComplexInstance> waypointModelInstances = new ArrayList<>();

	public MeleeUI(final DataSource dataSource, final ExtendViewport uiViewport,
			final FreeTypeFontGenerator fontGenerator, final Scene uiScene, final Scene portraitScene,
			final CameraPreset[] cameraPresets, final CameraRates cameraRates, final War3MapViewer war3MapViewer,
			final RootFrameListener rootFrameListener, final CPlayerUnitOrderListener unitOrderListener) {
		this.dataSource = dataSource;
		this.uiViewport = uiViewport;
		this.fontGenerator = fontGenerator;
		this.uiScene = uiScene;
		this.portraitScene = portraitScene;
		this.war3MapViewer = war3MapViewer;
		this.rootFrameListener = rootFrameListener;
		this.unitOrderListener = unitOrderListener;

		this.cameraManager = new GameCameraManager(cameraPresets, cameraRates);

		this.cameraManager.setupCamera(war3MapViewer.worldScene);
		this.localPlayer = this.war3MapViewer.simulation.getPlayer(war3MapViewer.getLocalPlayerIndex());
		final float[] startLocation = this.localPlayer.getStartLocation();
		this.cameraManager.target.x = startLocation[0];
		this.cameraManager.target.y = startLocation[1];

		this.activeButtonTexture = ImageUtils.getBLPTexture(war3MapViewer.mapMpq,
				"UI\\Widgets\\Console\\Human\\CommandButton\\human-activebutton.blp");
		this.activeCommandUnitTargetFilter = new ActiveCommandUnitTargetFilter();
		this.widthRatioCorrection = this.uiViewport.getMinWorldWidth() / 1600f;
		this.heightRatioCorrection = this.uiViewport.getMinWorldHeight() / 1200f;
		this.rallyPositioningVisitor = new RallyPositioningVisitor();
		this.cursorTargetSetupVisitor = new CursorTargetSetupVisitor();

		this.localPlayer.addStateListener(this);
	}

	private MeleeUIMinimap createMinimap(final War3MapViewer war3MapViewer) {
		final Rectangle minimapDisplayArea = new Rectangle(18.75f * this.widthRatioCorrection,
				13.75f * this.heightRatioCorrection, 278.75f * this.widthRatioCorrection,
				276.25f * this.heightRatioCorrection);
		Texture minimapTexture = null;
		if (war3MapViewer.dataSource.has("war3mapMap.tga")) {
			try {
				minimapTexture = ImageUtils.getTextureNoColorCorrection(TgaFile.readTGA("war3mapMap.tga",
						war3MapViewer.dataSource.getResourceAsStream("war3mapMap.tga")));
			}
			catch (final IOException e) {
				System.err.println("Could not load minimap TGA file");
				e.printStackTrace();
			}
		}
		else if (war3MapViewer.dataSource.has("war3mapMap.blp")) {
			try {
				minimapTexture = ImageUtils
						.getTexture(ImageIO.read(war3MapViewer.dataSource.getResourceAsStream("war3mapMap.blp")));
			}
			catch (final IOException e) {
				System.err.println("Could not load minimap BLP file");
				e.printStackTrace();
			}
		}
		final Texture[] teamColors = new Texture[WarsmashConstants.MAX_PLAYERS];
		for (int i = 0; i < teamColors.length; i++) {
			teamColors[i] = ImageUtils.getBLPTexture(war3MapViewer.dataSource,
					"ReplaceableTextures\\" + ReplaceableIds.getPathString(1) + ReplaceableIds.getIdString(i) + ".blp");
		}
		final Rectangle playableMapArea = war3MapViewer.terrain.getPlayableMapArea();
		return new MeleeUIMinimap(minimapDisplayArea, playableMapArea, minimapTexture, teamColors);
	}

	/**
	 * Called "main" because this was originally written in JASS so that maps could
	 * override it, and I may convert it back to the JASS at some point.
	 */
	public void main() {
		// =================================
		// Load skins and templates
		// =================================
		final CRace race = this.localPlayer.getRace();
		final int racialSkinIndex;
		int racialCommandIndex;
		if (race == null) {
			racialSkinIndex = 1;
			racialCommandIndex = 0;
		}
		else {
			switch (race) {
			case HUMAN:
				racialSkinIndex = 1;
				racialCommandIndex = 0;
				break;
			case ORC:
				racialSkinIndex = 0;
				racialCommandIndex = 1;
				break;
			case NIGHTELF:
				racialSkinIndex = 2;
				racialCommandIndex = 3;
				break;
			case UNDEAD:
				racialSkinIndex = 3;
				racialCommandIndex = 2;
				break;
			case DEMON:
			case OTHER:
			default:
				racialSkinIndex = -1;
				racialCommandIndex = 0;
				break;
			}
		}
		this.rootFrame = new GameUI(this.dataSource, GameUI.loadSkin(this.dataSource, racialSkinIndex), this.uiViewport,
				this.fontGenerator, this.uiScene, this.war3MapViewer, racialCommandIndex);
		this.rootFrameListener.onCreate(this.rootFrame);
		try {
			this.rootFrame.loadTOCFile("UI\\FrameDef\\FrameDef.toc");
		}
		catch (final IOException exc) {
			throw new IllegalStateException("Unable to load FrameDef.toc", exc);
		}
		try {
			this.rootFrame.loadTOCFile("UI\\FrameDef\\SmashFrameDef.toc");
		}
		catch (final IOException exc) {
			throw new IllegalStateException("Unable to load SmashFrameDef.toc", exc);
		}
		this.damageBackdrops = new InfoPanelIconBackdrops(CAttackType.values(), this.rootFrame, "Damage", "Neutral");
		this.defenseBackdrops = new InfoPanelIconBackdrops(CDefenseType.values(), this.rootFrame, "Armor", "Neutral");

		// =================================
		// Load major UI components
		// =================================
		// Console UI is the background with the racial theme
		this.consoleUI = this.rootFrame.createSimpleFrame("ConsoleUI", this.rootFrame, 0);
		this.consoleUI.setSetAllPoints(true);

		// Resource bar is a 3 part bar with Gold, Lumber, and Food.
		// Its template does not specify where to put it, so we must
		// put it in the "TOPRIGHT" corner.
		this.resourceBar = this.rootFrame.createSimpleFrame("ResourceBarFrame", this.consoleUI, 0);
		this.resourceBar.addSetPoint(new SetPoint(FramePoint.TOPRIGHT, this.consoleUI, FramePoint.TOPRIGHT, 0, 0));
		this.resourceBarGoldText = (StringFrame) this.rootFrame.getFrameByName("ResourceBarGoldText", 0);
		goldChanged();
		this.resourceBarLumberText = (StringFrame) this.rootFrame.getFrameByName("ResourceBarLumberText", 0);
		lumberChanged();
		this.resourceBarSupplyText = (StringFrame) this.rootFrame.getFrameByName("ResourceBarSupplyText", 0);
		foodChanged();
		this.resourceBarUpkeepText = (StringFrame) this.rootFrame.getFrameByName("ResourceBarUpkeepText", 0);
		upkeepChanged();

		// Create the Time Indicator (clock)
		this.timeIndicator = (SpriteFrame) this.rootFrame.createFrame("TimeOfDayIndicator", this.rootFrame, 0, 0);
		this.timeIndicator.setSequence(0); // play the stand
		this.timeIndicator.setAnimationSpeed(0.0f); // do not advance automatically

		// Create the unit portrait stuff
		this.portrait = new Portrait(this.war3MapViewer, this.portraitScene);
		positionPortrait();
		this.unitPortrait = this.rootFrame.createSimpleFrame("UnitPortrait", this.consoleUI, 0);
		this.unitLifeText = (StringFrame) this.rootFrame.getFrameByName("UnitPortraitHitPointText", 0);
		this.unitManaText = (StringFrame) this.rootFrame.getFrameByName("UnitPortraitManaPointText", 0);

		final float infoPanelUnitDetailWidth = GameUI.convertY(this.uiViewport, 0.180f);
		final float infoPanelUnitDetailHeight = GameUI.convertY(this.uiViewport, 0.105f);
		this.smashSimpleInfoPanel = this.rootFrame.createSimpleFrame("SmashSimpleInfoPanel", this.rootFrame, 0);
		this.smashSimpleInfoPanel
				.addAnchor(new AnchorDefinition(FramePoint.BOTTOM, 0, GameUI.convertY(this.uiViewport, 0.0f)));
		this.smashSimpleInfoPanel.setWidth(infoPanelUnitDetailWidth);
		this.smashSimpleInfoPanel.setHeight(infoPanelUnitDetailHeight);

		// Create Simple Info Unit Detail
		this.simpleInfoPanelUnitDetail = this.rootFrame.createSimpleFrame("SimpleInfoPanelUnitDetail",
				this.smashSimpleInfoPanel, 0);
		this.simpleNameValue = (StringFrame) this.rootFrame.getFrameByName("SimpleNameValue", 0);
		this.simpleClassValue = (StringFrame) this.rootFrame.getFrameByName("SimpleClassValue", 0);
		this.simpleBuildingActionLabel = (StringFrame) this.rootFrame.getFrameByName("SimpleBuildingActionLabel", 0);
		this.simpleBuildTimeIndicator = (SimpleStatusBarFrame) this.rootFrame.getFrameByName("SimpleBuildTimeIndicator",
				0);
		final TextureFrame simpleBuildTimeIndicatorBar = this.simpleBuildTimeIndicator.getBarFrame();
		simpleBuildTimeIndicatorBar.setTexture("SimpleBuildTimeIndicator", this.rootFrame);
		final TextureFrame simpleBuildTimeIndicatorBorder = this.simpleBuildTimeIndicator.getBorderFrame();
		simpleBuildTimeIndicatorBorder.setTexture("SimpleBuildTimeIndicatorBorder", this.rootFrame);
		final float buildTimeIndicatorWidth = GameUI.convertX(this.uiViewport, 0.10538f);
		final float buildTimeIndicatorHeight = GameUI.convertY(this.uiViewport, 0.0103f);
		this.simpleBuildTimeIndicator.setWidth(buildTimeIndicatorWidth);
		this.simpleBuildTimeIndicator.setHeight(buildTimeIndicatorHeight);

		// Create Simple Info Panel Building Detail
		this.simpleInfoPanelBuildingDetail = this.rootFrame.createSimpleFrame("SimpleInfoPanelBuildingDetail",
				this.smashSimpleInfoPanel, 0);
		this.simpleBuildingNameValue = (StringFrame) this.rootFrame.getFrameByName("SimpleBuildingNameValue", 0);
		this.simpleBuildingDescriptionValue = (StringFrame) this.rootFrame
				.getFrameByName("SimpleBuildingDescriptionValue", 0);
		this.simpleBuildingBuildingActionLabel = (StringFrame) this.rootFrame
				.getFrameByName("SimpleBuildingActionLabel", 0);
		this.simpleBuildingBuildTimeIndicator = (SimpleStatusBarFrame) this.rootFrame
				.getFrameByName("SimpleBuildTimeIndicator", 0);
		final TextureFrame simpleBuildingBuildTimeIndicatorBar = this.simpleBuildingBuildTimeIndicator.getBarFrame();
		simpleBuildingBuildTimeIndicatorBar.setTexture("SimpleBuildTimeIndicator", this.rootFrame);
		final TextureFrame simpleBuildingBuildTimeIndicatorBorder = this.simpleBuildingBuildTimeIndicator
				.getBorderFrame();
		simpleBuildingBuildTimeIndicatorBorder.setTexture("SimpleBuildTimeIndicatorBorder", this.rootFrame);
		this.simpleBuildingBuildTimeIndicator.setWidth(buildTimeIndicatorWidth);
		this.simpleBuildingBuildTimeIndicator.setHeight(buildTimeIndicatorHeight);
		this.simpleInfoPanelBuildingDetail.setVisible(false);
		final TextureFrame simpleBuildQueueBackdrop = (TextureFrame) this.rootFrame
				.getFrameByName("SimpleBuildQueueBackdrop", 0);
		simpleBuildQueueBackdrop.setWidth(infoPanelUnitDetailWidth);
		simpleBuildQueueBackdrop.setHeight(infoPanelUnitDetailWidth * 0.5f);

		this.queueIconFrames[0] = new QueueIcon("SmashBuildQueueIcon0", this.smashSimpleInfoPanel, this, 0);
		final TextureFrame queueIconFrameBackdrop0 = new TextureFrame("SmashBuildQueueIcon0Backdrop",
				this.queueIconFrames[0], false, new Vector4Definition(0, 1, 0, 1));
		queueIconFrameBackdrop0
				.addSetPoint(new SetPoint(FramePoint.CENTER, this.queueIconFrames[0], FramePoint.CENTER, 0, 0));
		this.queueIconFrames[0].set(queueIconFrameBackdrop0);
		this.queueIconFrames[0]
				.addSetPoint(new SetPoint(FramePoint.CENTER, this.smashSimpleInfoPanel, FramePoint.BOTTOMLEFT,
						(infoPanelUnitDetailWidth * (15 + 19f)) / 256, (infoPanelUnitDetailWidth * (66 + 19f)) / 256));
		final float frontQueueIconWidth = (infoPanelUnitDetailWidth * 38) / 256;
		this.queueIconFrames[0].setWidth(frontQueueIconWidth);
		this.queueIconFrames[0].setHeight(frontQueueIconWidth);
		queueIconFrameBackdrop0.setWidth(frontQueueIconWidth);
		queueIconFrameBackdrop0.setHeight(frontQueueIconWidth);
		this.rootFrame.add(this.queueIconFrames[0]);

		for (int i = 1; i < this.queueIconFrames.length; i++) {
			this.queueIconFrames[i] = new QueueIcon("SmashBuildQueueIcon" + i, this.smashSimpleInfoPanel, this, i);
			final TextureFrame queueIconFrameBackdrop = new TextureFrame("SmashBuildQueueIcon" + i + "Backdrop",
					this.queueIconFrames[i], false, new Vector4Definition(0, 1, 0, 1));
			this.queueIconFrames[i].set(queueIconFrameBackdrop);
			queueIconFrameBackdrop
					.addSetPoint(new SetPoint(FramePoint.CENTER, this.queueIconFrames[i], FramePoint.CENTER, 0, 0));
			this.queueIconFrames[i].addSetPoint(new SetPoint(FramePoint.CENTER, this.smashSimpleInfoPanel,
					FramePoint.BOTTOMLEFT, (infoPanelUnitDetailWidth * (13 + 14.5f + (40 * (i - 1)))) / 256,
					(infoPanelUnitDetailWidth * (24 + 14.5f)) / 256));
			final float queueIconWidth = (infoPanelUnitDetailWidth * 29) / 256;
			this.queueIconFrames[i].setWidth(queueIconWidth);
			this.queueIconFrames[i].setHeight(queueIconWidth);
			queueIconFrameBackdrop.setWidth(queueIconWidth);
			queueIconFrameBackdrop.setHeight(queueIconWidth);
			this.rootFrame.add(this.queueIconFrames[i]);
		}
		this.selectWorkerInsideFrame = new QueueIcon("SmashBuildQueueWorkerIcon", this.smashSimpleInfoPanel, this, 1);
		final TextureFrame selectWorkerInsideIconFrameBackdrop = new TextureFrame("SmashBuildQueueWorkerIconBackdrop",
				this.queueIconFrames[0], false, new Vector4Definition(0, 1, 0, 1));
		this.selectWorkerInsideFrame.set(selectWorkerInsideIconFrameBackdrop);
		selectWorkerInsideIconFrameBackdrop
				.addSetPoint(new SetPoint(FramePoint.CENTER, this.selectWorkerInsideFrame, FramePoint.CENTER, 0, 0));
		this.selectWorkerInsideFrame
				.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.queueIconFrames[1], FramePoint.TOPLEFT, 0, 0));
		this.selectWorkerInsideFrame.setWidth(frontQueueIconWidth);
		this.selectWorkerInsideFrame.setHeight(frontQueueIconWidth);
		selectWorkerInsideIconFrameBackdrop.setWidth(frontQueueIconWidth);
		selectWorkerInsideIconFrameBackdrop.setHeight(frontQueueIconWidth);
		this.rootFrame.add(this.selectWorkerInsideFrame);

		this.smashAttack1IconWrapper = (SimpleFrame) this.rootFrame.createSimpleFrame("SmashSimpleInfoPanelIconDamage",
				this.simpleInfoPanelUnitDetail, 0);
		this.smashAttack1IconWrapper.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail,
				FramePoint.TOPLEFT, 0, GameUI.convertY(this.uiViewport, -0.030125f)));
		this.smashAttack1IconWrapper.setWidth(GameUI.convertX(this.uiViewport, 0.1f));
		this.smashAttack1IconWrapper.setHeight(GameUI.convertY(this.uiViewport, 0.030125f));
		this.attack1Icon = this.rootFrame.createSimpleFrame("SimpleInfoPanelIconDamage", this.smashAttack1IconWrapper,
				0);
		this.attack1IconBackdrop = (TextureFrame) this.rootFrame.getFrameByName("InfoPanelIconBackdrop", 0);
		this.attack1InfoPanelIconValue = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconValue", 0);
		this.attack1InfoPanelIconLevel = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconLevel", 0);

		this.smashAttack2IconWrapper = (SimpleFrame) this.rootFrame.createSimpleFrame("SmashSimpleInfoPanelIconDamage",
				this.simpleInfoPanelUnitDetail, 0);
		this.smashAttack2IconWrapper
				.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail, FramePoint.TOPLEFT,
						GameUI.convertX(this.uiViewport, 0.1f), GameUI.convertY(this.uiViewport, -0.030125f)));
		this.smashAttack2IconWrapper.setWidth(GameUI.convertX(this.uiViewport, 0.1f));
		this.smashAttack2IconWrapper.setHeight(GameUI.convertY(this.uiViewport, 0.030125f));
		this.attack2Icon = this.rootFrame.createSimpleFrame("SimpleInfoPanelIconDamage", this.smashAttack2IconWrapper,
				1);
		this.attack2IconBackdrop = (TextureFrame) this.rootFrame.getFrameByName("InfoPanelIconBackdrop", 1);
		this.attack2InfoPanelIconValue = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconValue", 1);
		this.attack2InfoPanelIconLevel = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconLevel", 1);

		this.smashArmorIconWrapper = (SimpleFrame) this.rootFrame.createSimpleFrame("SmashSimpleInfoPanelIconArmor",
				this.simpleInfoPanelUnitDetail, 0);
		this.smashArmorIconWrapper.addSetPoint(new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail,
				FramePoint.TOPLEFT, GameUI.convertX(this.uiViewport, 0f), GameUI.convertY(this.uiViewport, -0.06025f)));
		this.smashArmorIconWrapper.setWidth(GameUI.convertX(this.uiViewport, 0.1f));
		this.smashArmorIconWrapper.setHeight(GameUI.convertY(this.uiViewport, 0.030125f));
		this.armorIcon = this.rootFrame.createSimpleFrame("SimpleInfoPanelIconArmor", this.smashArmorIconWrapper, 0);
		this.armorIconBackdrop = (TextureFrame) this.rootFrame.getFrameByName("InfoPanelIconBackdrop", 0);
		this.armorInfoPanelIconValue = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconValue", 0);
		this.armorInfoPanelIconLevel = (StringFrame) this.rootFrame.getFrameByName("InfoPanelIconLevel", 0);

		this.inventoryCover = this.rootFrame.createSimpleFrame("SmashConsoleInventoryCover", this.rootFrame, 0);

		final Element fontHeights = this.war3MapViewer.miscData.get("FontHeights");
		final float worldFrameMessageFontHeight = fontHeights.getFieldFloatValue("WorldFrameMessage");
		this.errorMessageFrame = this.rootFrame.createStringFrame("SmashErrorMessageFrame", this.rootFrame,
				new Color(0xFFCC00FF), TextJustify.LEFT, TextJustify.MIDDLE, worldFrameMessageFontHeight);
		this.errorMessageFrame.addAnchor(new AnchorDefinition(FramePoint.BOTTOMLEFT,
				GameUI.convertX(this.uiViewport, 0.212f), GameUI.convertY(this.uiViewport, 0.182f)));
		this.errorMessageFrame.setWidth(GameUI.convertX(this.uiViewport, 0.25f));
		this.errorMessageFrame.setHeight(GameUI.convertY(this.uiViewport, worldFrameMessageFontHeight));

		this.errorMessageFrame.setFontShadowColor(new Color(0f, 0f, 0f, 0.9f));
		this.errorMessageFrame.setFontShadowOffsetX(GameUI.convertX(this.uiViewport, 0.001f));
		this.errorMessageFrame.setFontShadowOffsetY(GameUI.convertY(this.uiViewport, -0.001f));
		this.errorMessageFrame.setVisible(false);

		int commandButtonIndex = 0;
		for (int j = 0; j < COMMAND_CARD_HEIGHT; j++) {
			for (int i = 0; i < COMMAND_CARD_WIDTH; i++) {
				final CommandCardIcon commandCardIcon = new CommandCardIcon("SmashCommandButton_" + commandButtonIndex,
						this.rootFrame, this);
				this.rootFrame.add(commandCardIcon);
				final TextureFrame iconFrame = new TextureFrame("SmashCommandButton_" + (commandButtonIndex) + "_Icon",
						this.rootFrame, false, null);
				final FilterModeTextureFrame activeHighlightFrame = new FilterModeTextureFrame(
						"SmashCommandButton_" + (commandButtonIndex) + "_ActiveHighlight", this.rootFrame, true, null);
				activeHighlightFrame.setFilterMode(FilterMode.ADDALPHA);
				final SpriteFrame cooldownFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE",
						"SmashCommandButton_" + (commandButtonIndex) + "_Cooldown", this.rootFrame, "", 0);
				final SpriteFrame autocastFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE",
						"SmashCommandButton_" + (commandButtonIndex) + "_Autocast", this.rootFrame, "", 0);
				commandCardIcon.addAnchor(new AnchorDefinition(FramePoint.BOTTOMLEFT,
						GameUI.convertX(this.uiViewport, 0.6175f + (0.0434f * i)),
						GameUI.convertY(this.uiViewport, 0.095f - (0.044f * j))));
				commandCardIcon.setWidth(GameUI.convertX(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				commandCardIcon.setHeight(GameUI.convertY(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				iconFrame.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				iconFrame.setWidth(GameUI.convertX(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				iconFrame.setHeight(GameUI.convertY(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				iconFrame.setTexture(ImageUtils.DEFAULT_ICON_PATH, this.rootFrame);
				activeHighlightFrame
						.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				activeHighlightFrame.setWidth(GameUI.convertX(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				activeHighlightFrame.setHeight(GameUI.convertY(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				activeHighlightFrame.setTexture("CommandButtonActiveHighlight", this.rootFrame);
				cooldownFrame.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				this.rootFrame.setSpriteFrameModel(cooldownFrame, this.rootFrame.getSkinField("CommandButtonCooldown"));
				cooldownFrame.setWidth(GameUI.convertX(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				cooldownFrame.setHeight(GameUI.convertY(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				autocastFrame.addSetPoint(new SetPoint(FramePoint.CENTER, commandCardIcon, FramePoint.CENTER, 0, 0));
				this.rootFrame.setSpriteFrameModel(autocastFrame, this.rootFrame.getSkinField("CommandButtonAutocast"));
				autocastFrame.setWidth(GameUI.convertX(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				autocastFrame.setHeight(GameUI.convertY(this.uiViewport, DEFAULT_COMMAND_CARD_ICON_WIDTH));
				commandCardIcon.set(iconFrame, activeHighlightFrame, cooldownFrame, autocastFrame);
				this.commandCard[j][i] = commandCardIcon;
				commandCardIcon.setCommandButton(null);
				commandButtonIndex++;
			}
		}

		this.tooltipFrame = this.rootFrame.createFrame("SmashToolTip", this.rootFrame, 0, 0);
		this.tooltipFrame.addAnchor(new AnchorDefinition(FramePoint.BOTTOMRIGHT, GameUI.convertX(this.uiViewport, 0.f),
				GameUI.convertY(this.uiViewport, 0.176f)));
		this.tooltipFrame.setWidth(GameUI.convertX(this.uiViewport, 0.176f));
		this.tooltipText = (StringFrame) this.rootFrame.getFrameByName("SmashToolTipText", 0);
		this.tooltipFrame.setVisible(false);
//		this.tooltipFrame = this.rootFrame.createFrameByType("BACKDROP", "SmashToolTipBackdrop", this.rootFrame, "", 0);

		this.cursorFrame = (SpriteFrame) this.rootFrame.createFrameByType("SPRITE", "SmashCursorFrame", this.rootFrame,
				"", 0);
		this.rootFrame.setSpriteFrameModel(this.cursorFrame, this.rootFrame.getSkinField("Cursor"));
		this.cursorFrame.setSequence("Normal");
		this.cursorFrame.setZDepth(-1.0f);
		Gdx.input.setCursorCatched(true);

		this.meleeUIMinimap = createMinimap(this.war3MapViewer);

		this.meleeUIAbilityActivationReceiver = new MeleeUIAbilityActivationReceiver(
				new AbilityActivationErrorHandler(this.rootFrame.getErrorString("NoGold"),
						this.war3MapViewer.getUiSounds().getSound(this.rootFrame.getSkinField("NoGoldSound"))),
				new AbilityActivationErrorHandler(this.rootFrame.getErrorString("NoLumber"),
						this.war3MapViewer.getUiSounds().getSound(this.rootFrame.getSkinField("NoLumberSound"))),
				new AbilityActivationErrorHandler(this.rootFrame.getErrorString("NoFood"),
						this.war3MapViewer.getUiSounds().getSound(this.rootFrame.getSkinField("NoFoodSound"))),
				new AbilityActivationErrorHandler("", this.war3MapViewer.getUiSounds().getSound("InterfaceError")));

		final MdxModel rallyModel = (MdxModel) this.war3MapViewer.load(
				War3MapViewer.mdx(this.rootFrame.getSkinField("RallyIndicatorDst")), this.war3MapViewer.mapPathSolver,
				this.war3MapViewer.solverParams);
		this.rallyPointInstance = (MdxComplexInstance) rallyModel.addInstance();
		this.rallyPointInstance.rotate(RenderUnit.tempQuat.setFromAxis(RenderMathUtils.VEC3_UNIT_Z,
				this.war3MapViewer.simulation.getGameplayConstants().getBuildingAngle()));
		this.rallyPointInstance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
		SequenceUtils.randomStandSequence(this.rallyPointInstance);
		this.rallyPointInstance.hide();
		this.waypointModel = (MdxModel) this.war3MapViewer.load(
				War3MapViewer.mdx(this.rootFrame.getSkinField("WaypointIndicator")), this.war3MapViewer.mapPathSolver,
				this.war3MapViewer.solverParams);

		this.rootFrame.positionBounds(this.rootFrame, this.uiViewport);

		selectUnit(null);
	}

	@Override
	public void startUsingAbility(final int abilityHandleId, final int orderId, final boolean rightClick) {
		// TODO not O(N)
		if (this.selectedUnit == null) {
			return;
		}
		if (orderId == 0) {
			return;
		}
		CAbilityView abilityToUse = null;
		for (final CAbility ability : this.selectedUnit.getSimulationUnit().getAbilities()) {
			if (ability.getHandleId() == abilityHandleId) {
				abilityToUse = ability;
				break;
			}
		}
		if (abilityToUse != null) {
			abilityToUse.checkCanUse(this.war3MapViewer.simulation, this.selectedUnit.getSimulationUnit(), orderId,
					this.meleeUIAbilityActivationReceiver.reset(this, this.war3MapViewer.worldScene.audioContext,
							this.selectedUnit));
			if (this.meleeUIAbilityActivationReceiver.isUseOk()) {
				final BooleanAbilityTargetCheckReceiver<Void> noTargetReceiver = BooleanAbilityTargetCheckReceiver
						.<Void>getInstance().reset();
				abilityToUse.checkCanTargetNoTarget(this.war3MapViewer.simulation,
						this.selectedUnit.getSimulationUnit(), orderId, noTargetReceiver);
				if (noTargetReceiver.isTargetable()) {
					this.unitOrderListener.issueImmediateOrder(this.selectedUnit.getSimulationUnit().getHandleId(),
							abilityHandleId, orderId, isShiftDown());
				}
				else {
					this.activeCommand = abilityToUse;
					this.activeCommandOrderId = orderId;
					this.activeCommandUnit = this.selectedUnit;
					clearAndRepopulateCommandCard();
				}
			}
		}
		else {
			this.unitOrderListener.issueImmediateOrder(this.selectedUnit.getSimulationUnit().getHandleId(),
					abilityHandleId, orderId, isShiftDown());
		}
		if (rightClick) {
			this.war3MapViewer.getUiSounds().getSound("AutoCastButtonClick").play(this.uiScene.audioContext, 0, 0);
		}
	}

	@Override
	public void openMenu(final int orderId) {
		if (orderId == 0) {
			this.subMenuOrderIdStack.clear();
			this.activeCommandUnit = null;
			this.activeCommand = null;
			this.activeCommandOrderId = -1;
		}
		else {
			this.subMenuOrderIdStack.add(orderId);
		}
		clearAndRepopulateCommandCard();
	}

	@Override
	public void showCommandError(final String message) {
		this.errorMessageFrame.setText(message);
		this.errorMessageFrame.setVisible(true);
		this.lastErrorMessageExpireTime = TimeUtils.millis() + WORLD_FRAME_MESSAGE_EXPIRE_MILLIS;
		this.lastErrorMessageFadeTime = TimeUtils.millis() + WORLD_FRAME_MESSAGE_FADEOUT_MILLIS;
		this.errorMessageFrame.setAlpha(1.0f);
	}

	@Override
	public void showCantPlaceError() {
		showCommandError(this.rootFrame.getErrorString("Cantplace"));
		this.war3MapViewer.getUiSounds().getSound(this.rootFrame.getSkinField("CantPlaceSound"))
				.play(this.uiScene.audioContext, 0, 0);
	}

	public void update(final float deltaTime) {
		this.portrait.update();

		final int baseMouseX = Gdx.input.getX();
		int mouseX = baseMouseX;
		final int baseMouseY = Gdx.input.getY();
		int mouseY = baseMouseY;
		final int minX = this.uiViewport.getScreenX();
		final int maxX = minX + this.uiViewport.getScreenWidth();
		final int minY = this.uiViewport.getScreenY();
		final int maxY = minY + this.uiViewport.getScreenHeight();
		final boolean left = mouseX <= (minX + 3);
		final boolean right = mouseX >= (maxX - 3);
		final boolean up = mouseY <= (minY + 3);
		final boolean down = mouseY >= (maxY - 3);
		this.cameraManager.applyVelocity(deltaTime, up, down, left, right);

		mouseX = Math.max(minX, Math.min(maxX, mouseX));
		mouseY = Math.max(minY, Math.min(maxY, mouseY));
		if (Gdx.input.isCursorCatched()) {
			Gdx.input.setCursorPosition(mouseX, mouseY);
		}

		screenCoordsVector.set(mouseX, mouseY);
		this.uiViewport.unproject(screenCoordsVector);
		this.cursorFrame.setFramePointX(FramePoint.LEFT, screenCoordsVector.x);
		this.cursorFrame.setFramePointY(FramePoint.BOTTOM, screenCoordsVector.y);

		if (this.activeCommand != null) {
			this.activeCommand.visit(this.cursorTargetSetupVisitor.reset(baseMouseX, baseMouseY));
		}
		else {
			if (this.cursorModelInstance != null) {
				this.cursorModelInstance.detach();
				this.cursorModelInstance = null;
				this.cursorFrame.setVisible(true);
			}
			if (this.placementCursor != null) {
				this.placementCursor.destroy(Gdx.gl30, this.war3MapViewer.terrain.centerOffset);
				this.placementCursor = null;
				this.cursorFrame.setVisible(true);
			}
			if (this.cursorModelUnderneathPathingRedGreenSplatModel != null) {
				this.war3MapViewer.terrain.removeSplatBatchModel(BUILDING_PATHING_PREVIEW_KEY);
				this.cursorModelUnderneathPathingRedGreenSplatModel = null;
			}
			if (down) {
				if (left) {
					this.cursorFrame.setSequence("Scroll Down Left");
				}
				else if (right) {
					this.cursorFrame.setSequence("Scroll Down Right");
				}
				else {
					this.cursorFrame.setSequence("Scroll Down");
				}
			}
			else if (up) {
				if (left) {
					this.cursorFrame.setSequence("Scroll Up Left");
				}
				else if (right) {
					this.cursorFrame.setSequence("Scroll Up Right");
				}
				else {
					this.cursorFrame.setSequence("Scroll Up");
				}
			}
			else if (left) {
				this.cursorFrame.setSequence("Scroll Left");
			}
			else if (right) {
				this.cursorFrame.setSequence("Scroll Right");
			}
			else {
				this.cursorFrame.setSequence("Normal");
			}
		}
		if (this.selectedUnit != null) {
			if (this.simpleBuildTimeIndicator.isVisible()) {
				this.simpleBuildTimeIndicator
						.setValue(Math.min(this.selectedUnit.getSimulationUnit().getConstructionProgress()
								/ this.selectedUnit.getSimulationUnit().getUnitType().getBuildTime(), 0.99f));
			}
			if (this.simpleBuildingBuildTimeIndicator.isVisible()) {
				this.simpleBuildingBuildTimeIndicator
						.setValue(Math.min(
								this.selectedUnit.getSimulationUnit().getConstructionProgress() / this.selectedUnit
										.getSimulationUnit().getBuildQueueTimeRemaining(this.war3MapViewer.simulation),
								0.99f));
			}
		}

		final float groundHeight = Math.max(
				this.war3MapViewer.terrain.getGroundHeight(this.cameraManager.target.x, this.cameraManager.target.y),
				this.war3MapViewer.terrain.getWaterHeight(this.cameraManager.target.x, this.cameraManager.target.y));
		this.cameraManager.updateTargetZ(groundHeight);
		this.cameraManager.updateCamera();
		final long currentMillis = TimeUtils.millis();
		if (currentMillis > this.lastErrorMessageExpireTime) {
			this.errorMessageFrame.setVisible(false);
		}
		else if (currentMillis > this.lastErrorMessageFadeTime) {
			final float fadeAlpha = (this.lastErrorMessageExpireTime - currentMillis)
					/ (float) WORLD_FRAME_MESSAGE_FADE_DURATION;
			this.errorMessageFrame.setAlpha(fadeAlpha);
		}
	}

	public void render(final SpriteBatch batch, final BitmapFont font20, final GlyphLayout glyphLayout) {
		this.rootFrame.render(batch, font20, glyphLayout);
		if (this.selectedUnit != null) {
			font20.setColor(Color.WHITE);

		}

		this.meleeUIMinimap.render(batch, this.war3MapViewer.units);
		this.timeIndicator.setFrameByRatio(this.war3MapViewer.simulation.getGameTimeOfDay()
				/ this.war3MapViewer.simulation.getGameplayConstants().getGameDayHours());
	}

	public void portraitTalk() {
		this.portrait.talk();
	}

	private final class CursorTargetSetupVisitor implements CAbilityVisitor<Void> {
		private int baseMouseX;
		private int baseMouseY;

		private CursorTargetSetupVisitor reset(final int baseMouseX, final int baseMouseY) {
			this.baseMouseX = baseMouseX;
			this.baseMouseY = baseMouseY;
			return this;
		}

		@Override
		public Void accept(final CAbilityAttack ability) {
			if (MeleeUI.this.activeCommandOrderId == OrderIds.attackground) {
				float radius = 0;
				for (final CUnitAttack attack : MeleeUI.this.activeCommandUnit.getSimulationUnit().getUnitType()
						.getAttacks()) {
					if (attack.getWeaponType().isAttackGroundSupported()) {
						if (attack instanceof CUnitAttackMissileSplash) {
							final int areaOfEffectSmallDamage = ((CUnitAttackMissileSplash) attack)
									.getAreaOfEffectSmallDamage();
							radius = areaOfEffectSmallDamage;
						}
					}
				}
				handlePlacementCursor(ability, radius);
			}
			else {
				handleTargetCursor(ability);
			}
			return null;
		}

		@Override
		public Void accept(final CAbilityMove ability) {
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityOrcBuild ability) {
			handleBuildCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityHumanBuild ability) {
			handleBuildCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityUndeadBuild ability) {
			handleBuildCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityNightElfBuild ability) {
			handleBuildCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityGeneric ability) {
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityColdArrows ability) {
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityNagaBuild ability) {
			handleBuildCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityNeutralBuild ability) {
			handleBuildCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityBuildInProgress ability) {
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityQueue ability) {
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final GenericSingleIconActiveAbility ability) {
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final GenericNoIconAbility ability) {
			// this should probably never happen
			handleTargetCursor(ability);
			return null;
		}

		@Override
		public Void accept(final CAbilityRally ability) {
			handleTargetCursor(ability);
			return null;
		}

		private void handleTargetCursor(final CAbility ability) {
			if (MeleeUI.this.cursorModelInstance != null) {
				MeleeUI.this.cursorModelInstance.detach();
				MeleeUI.this.cursorModelInstance = null;
				MeleeUI.this.cursorFrame.setVisible(true);
			}
			MeleeUI.this.cursorFrame.setSequence("Target");
		}

		private void handleBuildCursor(final AbstractCAbilityBuild ability) {
			boolean justLoaded = false;
			final War3MapViewer viewer = MeleeUI.this.war3MapViewer;
			if (MeleeUI.this.cursorModelInstance == null) {
				final MutableObjectData unitData = viewer.getAllObjectData().getUnits();
				final War3ID buildingTypeId = new War3ID(MeleeUI.this.activeCommandOrderId);
				MeleeUI.this.cursorBuildingUnitType = viewer.simulation.getUnitData().getUnitType(buildingTypeId);
				final String unitModelPath = viewer.getUnitModelPath(unitData.get(buildingTypeId));
				final MdxModel model = (MdxModel) viewer.load(unitModelPath, viewer.mapPathSolver, viewer.solverParams);
				MeleeUI.this.cursorModelInstance = (MdxComplexInstance) model.addInstance();
//				MeleeUI.this.cursorModelInstance.setVertexColor(new float[] { 1, 1, 1, 0.5f });
				final int playerColorIndex = viewer.simulation
						.getPlayer(MeleeUI.this.activeCommandUnit.getSimulationUnit().getPlayerIndex()).getColorIndex();
				MeleeUI.this.cursorModelInstance.setTeamColor(playerColorIndex);
				MeleeUI.this.cursorModelInstance.rotate(RenderUnit.tempQuat.setFromAxis(RenderMathUtils.VEC3_UNIT_Z,
						viewer.simulation.getGameplayConstants().getBuildingAngle()));
				MeleeUI.this.cursorModelInstance.setAnimationSpeed(0f);
				justLoaded = true;
				final CUnitType buildingUnitType = MeleeUI.this.cursorBuildingUnitType;
				MeleeUI.this.cursorModelPathing = buildingUnitType.getBuildingPathingPixelMap();

				if (MeleeUI.this.cursorModelPathing != null) {
					MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap = new Pixmap(
							MeleeUI.this.cursorModelPathing.getWidth(), MeleeUI.this.cursorModelPathing.getHeight(),
							Format.RGBA8888);
					MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.setBlending(Blending.None);
					MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmapTextureData = new PixmapTextureData(
							MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap, Format.RGBA8888, false, false);
					MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmapTexture = new Texture(
							MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmapTextureData);
					final ViewerTextureRenderable greenPixmap = new ViewerTextureRenderable.GdxViewerTextureRenderable(
							MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmapTexture);
					MeleeUI.this.cursorModelUnderneathPathingRedGreenSplatModel = new SplatModel(Gdx.gl30, greenPixmap,
							new ArrayList<>(), viewer.terrain.centerOffset, new ArrayList<>(), true, false);
					MeleeUI.this.cursorModelUnderneathPathingRedGreenSplatModel.color[3] = 0.20f;
				}
			}
			viewer.getClickLocation(clickLocationTemp, this.baseMouseX, Gdx.graphics.getHeight() - this.baseMouseY);
			if (MeleeUI.this.cursorModelPathing != null) {
				clickLocationTemp.x = (float) Math.floor(clickLocationTemp.x / 64f) * 64f;
				clickLocationTemp.y = (float) Math.floor(clickLocationTemp.y / 64f) * 64f;
				if (((MeleeUI.this.cursorModelPathing.getWidth() / 2) % 2) == 1) {
					clickLocationTemp.x += 32f;
				}
				if (((MeleeUI.this.cursorModelPathing.getHeight() / 2) % 2) == 1) {
					clickLocationTemp.y += 32f;
				}
				clickLocationTemp.z = viewer.terrain.getGroundHeight(clickLocationTemp.x, clickLocationTemp.y);

				final float halfRenderWidth = MeleeUI.this.cursorModelPathing.getWidth() * 16;
				final float halfRenderHeight = MeleeUI.this.cursorModelPathing.getHeight() * 16;
				for (int i = 0; i < MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.getWidth(); i++) {
					for (int j = 0; j < MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.getHeight(); j++) {
						boolean blocked = false;
						final short pathing = viewer.simulation.getPathingGrid().getPathing(
								(clickLocationTemp.x + (i * 32)) - halfRenderWidth,
								(clickLocationTemp.y + (j * 32)) - halfRenderHeight);
						for (final CBuildingPathingType preventedType : MeleeUI.this.cursorBuildingUnitType
								.getPreventedPathingTypes()) {
							if (PathingFlags.isPathingFlag(pathing, preventedType)) {
								blocked = true;
							}
						}
						for (final CBuildingPathingType requiredType : MeleeUI.this.cursorBuildingUnitType
								.getRequiredPathingTypes()) {
							if (!PathingFlags.isPathingFlag(pathing, requiredType)) {
								blocked = true;
							}
						}
						final int color = blocked ? Color.rgba8888(1, 0, 0, 1.0f) : Color.rgba8888(0, 1, 0, 1.0f);
						MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.drawPixel(i,
								MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmap.getHeight() - 1 - j, color);
					}
				}
				MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmapTexture
						.load(MeleeUI.this.cursorModelUnderneathPathingRedGreenPixmapTextureData);

				if (justLoaded) {
					viewer.terrain.addSplatBatchModel(BUILDING_PATHING_PREVIEW_KEY,
							MeleeUI.this.cursorModelUnderneathPathingRedGreenSplatModel);
					MeleeUI.this.placementCursor = MeleeUI.this.cursorModelUnderneathPathingRedGreenSplatModel.add(
							clickLocationTemp.x - halfRenderWidth, clickLocationTemp.y - halfRenderHeight,
							clickLocationTemp.x + halfRenderWidth, clickLocationTemp.y + halfRenderHeight, 10,
							viewer.terrain.centerOffset);
				}
				MeleeUI.this.placementCursor.setLocation(clickLocationTemp.x, clickLocationTemp.y,
						viewer.terrain.centerOffset);
			}
			MeleeUI.this.cursorModelInstance.setLocation(clickLocationTemp);
			SequenceUtils.randomSequence(MeleeUI.this.cursorModelInstance, PrimaryTag.STAND);
			MeleeUI.this.cursorFrame.setVisible(false);
			if (justLoaded) {
				MeleeUI.this.cursorModelInstance.setScene(viewer.worldScene);
			}
		}

		private void handlePlacementCursor(final CAbility ability, final float radius) {
			final War3MapViewer viewer = MeleeUI.this.war3MapViewer;
			viewer.getClickLocation(clickLocationTemp, this.baseMouseX, Gdx.graphics.getHeight() - this.baseMouseY);
			if (MeleeUI.this.placementCursor == null) {
				MeleeUI.this.placementCursor = viewer.terrain.addUberSplat(
						MeleeUI.this.rootFrame.getSkinField("PlacementCursor"), clickLocationTemp.x,
						clickLocationTemp.y, 10, radius, true, true);
			}
			MeleeUI.this.placementCursor.setLocation(clickLocationTemp.x, clickLocationTemp.y,
					viewer.terrain.centerOffset);
			MeleeUI.this.cursorFrame.setVisible(false);
		}
	}

	private final class RallyPositioningVisitor implements AbilityTargetVisitor<Void> {
		private MdxComplexInstance rallyPointInstance = null;

		public RallyPositioningVisitor reset(final MdxComplexInstance rallyPointInstance) {
			this.rallyPointInstance = rallyPointInstance;
			return this;
		}

		@Override
		public Void accept(final AbilityPointTarget target) {
			this.rallyPointInstance.setParent(null);
			final float rallyPointX = target.getX();
			final float rallyPointY = target.getY();
			this.rallyPointInstance.setLocation(rallyPointX, rallyPointY,
					MeleeUI.this.war3MapViewer.terrain.getGroundHeight(rallyPointX, rallyPointY));
			return null;
		}

		@Override
		public Void accept(final CUnit target) {
			final RenderUnit renderUnit = MeleeUI.this.war3MapViewer.getRenderPeer(target);
			final MdxModel model = (MdxModel) renderUnit.instance.model;
			int index = -1;
			for (int i = 0; i < model.attachments.size(); i++) {
				final Attachment attachment = model.attachments.get(i);
				if (attachment.getName().startsWith("sprite")) {
					index = i;
					break;
				}
			}
			if (index == -1) {
				for (int i = 0; i < model.attachments.size(); i++) {
					final Attachment attachment = model.attachments.get(i);
					if (attachment.getName().startsWith("overhead ref")) {
						index = i;
					}
				}
			}
			if (index != -1) {
				final MdxNode attachment = renderUnit.instance.getAttachment(index);
				this.rallyPointInstance.setParent(attachment);
				this.rallyPointInstance.setLocation(0, 0, 0);
			}
			else {
				this.rallyPointInstance.setParent(null);
				final float rallyPointX = target.getX();
				final float rallyPointY = target.getY();
				this.rallyPointInstance.setLocation(rallyPointX, rallyPointY,
						MeleeUI.this.war3MapViewer.terrain.getGroundHeight(rallyPointX, rallyPointY));
			}
			return null;
		}

		@Override
		public Void accept(final CDestructable target) {
			this.rallyPointInstance.setParent(null);
			final float rallyPointX = target.getX();
			final float rallyPointY = target.getY();
			this.rallyPointInstance.setLocation(rallyPointX, rallyPointY,
					MeleeUI.this.war3MapViewer.terrain.getGroundHeight(rallyPointX, rallyPointY));
			return null;
		}

		@Override
		public Void accept(final CItem target) {
			this.rallyPointInstance.setParent(null);
			final float rallyPointX = target.getX();
			final float rallyPointY = target.getY();
			this.rallyPointInstance.setLocation(rallyPointX, rallyPointY,
					MeleeUI.this.war3MapViewer.terrain.getGroundHeight(rallyPointX, rallyPointY));
			return null;
		}
	}

	private final class ActiveCommandUnitTargetFilter implements CUnitFilterFunction {
		@Override
		public boolean call(final CUnit unit) {
			final BooleanAbilityTargetCheckReceiver<CWidget> targetReceiver = BooleanAbilityTargetCheckReceiver
					.<CWidget>getInstance();
			MeleeUI.this.activeCommand.checkCanTarget(MeleeUI.this.war3MapViewer.simulation,
					MeleeUI.this.activeCommandUnit.getSimulationUnit(), MeleeUI.this.activeCommandOrderId, unit,
					targetReceiver);
			return targetReceiver.isTargetable();
		}
	}

	private static final class Portrait {
		private MdxComplexInstance modelInstance;
		private final PortraitCameraManager portraitCameraManager;
		private final Scene portraitScene;
		private final EnumSet<AnimationTokens.SecondaryTag> recycleSet = EnumSet
				.noneOf(AnimationTokens.SecondaryTag.class);
		private RenderUnit unit;

		public Portrait(final War3MapViewer war3MapViewer, final Scene portraitScene) {
			this.portraitScene = portraitScene;
			this.portraitCameraManager = new PortraitCameraManager();
			this.portraitCameraManager.setupCamera(this.portraitScene);
			this.portraitScene.camera.viewport(new Rectangle(100, 0, 6400, 48));
		}

		public void update() {
			this.portraitCameraManager.updateCamera();
			if ((this.modelInstance != null)
					&& (this.modelInstance.sequenceEnded || (this.modelInstance.sequence == -1))) {
				this.recycleSet.clear();
				this.recycleSet.addAll(this.unit.getSecondaryAnimationTags());
				SequenceUtils.randomSequence(this.modelInstance, PrimaryTag.PORTRAIT, this.recycleSet, true);
			}
		}

		public void talk() {
			this.recycleSet.clear();
			this.recycleSet.addAll(this.unit.getSecondaryAnimationTags());
			this.recycleSet.add(SecondaryTag.TALK);
			SequenceUtils.randomSequence(this.modelInstance, PrimaryTag.PORTRAIT, this.recycleSet, true);
		}

		public void setSelectedUnit(final RenderUnit unit) {
			this.unit = unit;
			if (unit == null) {
				if (this.modelInstance != null) {
					this.portraitScene.removeInstance(this.modelInstance);
				}
				this.modelInstance = null;
				this.portraitCameraManager.setModelInstance(null, null);
			}
			else {
				final MdxModel portraitModel = unit.portraitModel;
				if (portraitModel != null) {
					if (this.modelInstance != null) {
						this.portraitScene.removeInstance(this.modelInstance);
					}
					this.modelInstance = (MdxComplexInstance) portraitModel.addInstance();
					this.portraitCameraManager.setModelInstance(this.modelInstance, portraitModel);
					this.modelInstance.setSequenceLoopMode(SequenceLoopMode.NEVER_LOOP);
					this.modelInstance.setScene(this.portraitScene);
					this.modelInstance.setVertexColor(unit.instance.vertexColor);
					this.modelInstance.setTeamColor(unit.playerIndex);
				}
			}
		}
	}

	public void selectUnit(RenderUnit unit) {
		this.subMenuOrderIdStack.clear();
		if ((unit != null) && unit.getSimulationUnit().isDead()) {
			unit = null;
		}
		if (this.selectedUnit != null) {
			this.selectedUnit.getSimulationUnit().removeStateListener(this);
		}
		this.portrait.setSelectedUnit(unit);
		this.selectedUnit = unit;
		if (unit == null) {
			clearCommandCard();
			this.simpleNameValue.setText("");
			this.unitLifeText.setText("");
			this.unitManaText.setText("");
			this.simpleClassValue.setText("");
			this.simpleBuildingActionLabel.setText("");
			this.attack1Icon.setVisible(false);
			this.attack2Icon.setVisible(false);
			this.attack1InfoPanelIconLevel.setText("");
			this.attack2InfoPanelIconLevel.setText("");
			this.simpleBuildingBuildingActionLabel.setText("");
			this.simpleBuildingNameValue.setText("");
			this.armorIcon.setVisible(false);
			this.armorInfoPanelIconLevel.setText("");
			this.simpleBuildTimeIndicator.setVisible(false);
			this.simpleBuildingBuildTimeIndicator.setVisible(false);
			this.simpleInfoPanelBuildingDetail.setVisible(false);
			this.simpleInfoPanelUnitDetail.setVisible(false);
			for (final QueueIcon queueIconFrame : this.queueIconFrames) {
				queueIconFrame.setVisible(false);
			}
			this.selectWorkerInsideFrame.setVisible(false);
			this.rallyPointInstance.hide();
			this.rallyPointInstance.detach();
			repositionWaypointFlags(null);
		}
		else {
			unit.getSimulationUnit().addStateListener(this);
			reloadSelectedUnitUI(unit);
		}
	}

	@Override
	public void rallyPointChanged() {
		if (this.selectedUnit != null) {
			final CUnit simulationUnit = this.selectedUnit.getSimulationUnit();
			repositionRallyPoint(simulationUnit);
		}
	}

	private void repositionRallyPoint(final CUnit simulationUnit) {
		final AbilityTarget rallyPoint = simulationUnit.getRallyPoint();
		if (rallyPoint != null) {
			this.rallyPointInstance.setTeamColor(
					this.war3MapViewer.simulation.getPlayer(simulationUnit.getPlayerIndex()).getColorIndex());
			this.rallyPointInstance.show();
			this.rallyPointInstance.detach();
			rallyPoint.visit(this.rallyPositioningVisitor.reset(this.rallyPointInstance));
			this.rallyPointInstance.setScene(this.war3MapViewer.worldScene);
		}
		else {
			this.rallyPointInstance.hide();
			this.rallyPointInstance.detach();
		}
	}

	@Override
	public void waypointsChanged() {
		if (this.selectedUnit != null) {
			final CUnit simulationUnit = this.selectedUnit.getSimulationUnit();
			repositionWaypointFlags(simulationUnit);
		}
		else {
			repositionWaypointFlags(null);
		}
	}

	private void repositionWaypointFlags(final CUnit simulationUnit) {
		final Iterator<COrder> iterator;
		int orderIndex = 0;
		if (simulationUnit != null) {
			final Queue<COrder> orderQueue = simulationUnit.getOrderQueue();
			iterator = orderQueue.iterator();
			final COrder order = simulationUnit.getCurrentOrder();
			if ((order != null) && order.isQueued()) {
				final MdxComplexInstance waypointModelInstance = getOrCreateWaypointIndicator(orderIndex);
				final AbilityTarget target = order.getTarget(this.war3MapViewer.simulation);
				if (target != null) {
					waypointModelInstance.show();
					waypointModelInstance.detach();
					target.visit(this.rallyPositioningVisitor.reset(waypointModelInstance));
					waypointModelInstance.setScene(this.war3MapViewer.worldScene);
				}
				else {
					waypointModelInstance.hide();
					waypointModelInstance.detach();
				}
				orderIndex++;
			}
		}
		else {
			iterator = Collections.emptyIterator();
		}
		for (; (orderIndex < this.waypointModelInstances.size()) || (iterator.hasNext()); orderIndex++) {
			final MdxComplexInstance waypointModelInstance = getOrCreateWaypointIndicator(orderIndex);
			if (iterator.hasNext()) {
				final COrder order = iterator.next();
				final AbilityTarget target = order.getTarget(this.war3MapViewer.simulation);
				if (target != null) {
					waypointModelInstance.show();
					waypointModelInstance.detach();
					target.visit(this.rallyPositioningVisitor.reset(waypointModelInstance));
					waypointModelInstance.setScene(this.war3MapViewer.worldScene);
				}
				else {
					waypointModelInstance.hide();
					waypointModelInstance.detach();
				}
			}
			else {
				waypointModelInstance.hide();
				waypointModelInstance.detach();
			}
		}
	}

	private MdxComplexInstance getOrCreateWaypointIndicator(final int index) {
		while (index >= this.waypointModelInstances.size()) {
			final MdxComplexInstance waypointModelInstance = (MdxComplexInstance) this.waypointModel.addInstance();
			waypointModelInstance.rotate(RenderUnit.tempQuat.setFromAxis(RenderMathUtils.VEC3_UNIT_Z,
					this.war3MapViewer.simulation.getGameplayConstants().getBuildingAngle()));
			waypointModelInstance.setSequenceLoopMode(SequenceLoopMode.ALWAYS_LOOP);
			SequenceUtils.randomStandSequence(waypointModelInstance);
			waypointModelInstance.hide();
			this.waypointModelInstances.add(waypointModelInstance);
		}
		return this.waypointModelInstances.get(index);
	}

	private void reloadSelectedUnitUI(final RenderUnit unit) {
		final CUnit simulationUnit = unit.getSimulationUnit();
		this.unitLifeText.setText(
				FastNumberFormat.formatWholeNumber(simulationUnit.getLife()) + " / " + simulationUnit.getMaximumLife());
		final int maximumMana = simulationUnit.getMaximumMana();
		if (maximumMana > 0) {
			this.unitManaText
					.setText(FastNumberFormat.formatWholeNumber(simulationUnit.getMana()) + " / " + maximumMana);
		}
		else {
			this.unitManaText.setText("");
		}
		repositionRallyPoint(simulationUnit);
		repositionWaypointFlags(simulationUnit);
		if (simulationUnit.getBuildQueue()[0] != null) {
			for (int i = 0; i < this.queueIconFrames.length; i++) {
				final QueueItemType queueItemType = simulationUnit.getBuildQueueTypes()[i];
				if (queueItemType == null) {
					this.queueIconFrames[i].setVisible(false);
				}
				else {
					this.queueIconFrames[i].setVisible(true);
					switch (queueItemType) {
					case RESEARCH:
						final IconUI upgradeUI = this.war3MapViewer.getAbilityDataUI()
								.getUpgradeUI(simulationUnit.getBuildQueue()[i], 0);
						this.queueIconFrames[i].setTexture(upgradeUI.getIcon());
						break;
					case UNIT:
					default:
						final IconUI unitUI = this.war3MapViewer.getAbilityDataUI()
								.getUnitUI(simulationUnit.getBuildQueue()[i]);
						this.queueIconFrames[i].setTexture(unitUI.getIcon());
						break;
					}
				}
			}
			this.simpleInfoPanelBuildingDetail.setVisible(true);
			this.simpleInfoPanelUnitDetail.setVisible(false);
			this.simpleBuildingNameValue.setText(simulationUnit.getUnitType().getName());
			this.simpleBuildingDescriptionValue.setText("");

			this.simpleBuildingBuildTimeIndicator.setVisible(true);
			this.simpleBuildTimeIndicator.setVisible(false);
			if (simulationUnit.getBuildQueueTypes()[0] == QueueItemType.UNIT) {
				this.simpleBuildingBuildingActionLabel
						.setText(this.rootFrame.getTemplates().getDecoratedString("TRAINING"));
			}
			else {
				this.simpleBuildingBuildingActionLabel
						.setText(this.rootFrame.getTemplates().getDecoratedString("RESEARCHING"));
			}
			this.attack1Icon.setVisible(false);
			this.attack2Icon.setVisible(false);
			this.armorIcon.setVisible(false);
		}
		else {
			for (final QueueIcon queueIconFrame : this.queueIconFrames) {
				queueIconFrame.setVisible(false);
			}
			this.simpleInfoPanelBuildingDetail.setVisible(false);
			this.simpleInfoPanelUnitDetail.setVisible(true);
			this.simpleNameValue.setText(simulationUnit.getUnitType().getName());
			String classText = null;
			for (final CUnitClassification classification : simulationUnit.getClassifications()) {
				if ((classification == CUnitClassification.MECHANICAL) && simulationUnit.getUnitType().isBuilding()) {
					// buildings dont display MECHANICAL
					continue;
				}
				if (classification.getDisplayName() != null) {
					classText = classification.getDisplayName();
				}
			}
			if (classText != null) {
				this.simpleClassValue.setText(classText);
			}
			else {
				this.simpleClassValue.setText("");
			}

			final boolean anyAttacks = simulationUnit.getUnitType().getAttacks().size() > 0;
			final boolean constructing = simulationUnit.isConstructing();
			final UIFrame localArmorIcon = this.armorIcon;
			final TextureFrame localArmorIconBackdrop = this.armorIconBackdrop;
			final StringFrame localArmorInfoPanelIconValue = this.armorInfoPanelIconValue;
			if (anyAttacks && !constructing) {
				final CUnitAttack attackOne = simulationUnit.getUnitType().getAttacks().get(0);
				this.attack1Icon.setVisible(attackOne.isShowUI());
				this.attack1IconBackdrop.setTexture(this.damageBackdrops.getTexture(attackOne.getAttackType()));
				this.attack1InfoPanelIconValue.setText(attackOne.getMinDamage() + " - " + attackOne.getMaxDamage());
				if (simulationUnit.getUnitType().getAttacks().size() > 1) {
					final CUnitAttack attackTwo = simulationUnit.getUnitType().getAttacks().get(1);
					this.attack2Icon.setVisible(attackTwo.isShowUI());
					this.attack2IconBackdrop.setTexture(this.damageBackdrops.getTexture(attackTwo.getAttackType()));
					this.attack2InfoPanelIconValue.setText(attackTwo.getMinDamage() + " - " + attackTwo.getMaxDamage());
				}
				else {
					this.attack2Icon.setVisible(false);
				}

				this.smashArmorIconWrapper.addSetPoint(
						new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail, FramePoint.TOPLEFT,
								GameUI.convertX(this.uiViewport, 0f), GameUI.convertY(this.uiViewport, -0.06025f)));
				this.smashArmorIconWrapper.positionBounds(this.rootFrame, this.uiViewport);
				this.armorIcon.positionBounds(this.rootFrame, this.uiViewport);
			}
			else {
				this.attack1Icon.setVisible(false);
				this.attack2Icon.setVisible(false);

				this.smashArmorIconWrapper.addSetPoint(
						new SetPoint(FramePoint.TOPLEFT, this.simpleInfoPanelUnitDetail, FramePoint.TOPLEFT,
								GameUI.convertX(this.uiViewport, 0f), GameUI.convertY(this.uiViewport, -0.030125f)));
				this.smashArmorIconWrapper.positionBounds(this.rootFrame, this.uiViewport);
				this.armorIcon.positionBounds(this.rootFrame, this.uiViewport);
			}

			localArmorIcon.setVisible(!constructing);
			this.simpleBuildTimeIndicator.setVisible(constructing);
			this.simpleBuildingBuildTimeIndicator.setVisible(false);
			if (constructing) {
				this.simpleBuildingActionLabel
						.setText(this.rootFrame.getTemplates().getDecoratedString("CONSTRUCTING"));
				this.queueIconFrames[0].setVisible(true);
				this.queueIconFrames[0].setTexture(this.war3MapViewer.getAbilityDataUI()
						.getUnitUI(this.selectedUnit.getSimulationUnit().getTypeId()).getIcon());

				if (this.selectedUnit.getSimulationUnit().getWorkerInside() != null) {
					this.selectWorkerInsideFrame.setVisible(true);
					this.selectWorkerInsideFrame.setTexture(this.war3MapViewer.getAbilityDataUI()
							.getUnitUI(this.selectedUnit.getSimulationUnit().getWorkerInside().getTypeId()).getIcon());
				}
				else {
					this.selectWorkerInsideFrame.setVisible(false);
				}
			}
			else {
				this.simpleBuildingActionLabel.setText("");
				this.selectWorkerInsideFrame.setVisible(false);
			}
			final Texture defenseTexture = this.defenseBackdrops
					.getTexture(simulationUnit.getUnitType().getDefenseType());
			if (defenseTexture == null) {
				throw new RuntimeException(simulationUnit.getUnitType().getDefenseType() + " can't find texture!");
			}
			localArmorIconBackdrop.setTexture(defenseTexture);
			localArmorInfoPanelIconValue.setText(Integer.toString(simulationUnit.getDefense()));
		}
		clearAndRepopulateCommandCard();
	}

	private void clearCommandCard() {
		for (int j = 0; j < COMMAND_CARD_HEIGHT; j++) {
			for (int i = 0; i < COMMAND_CARD_WIDTH; i++) {
				this.commandCard[j][i].setCommandButton(null);
			}
		}
	}

	@Override
	public void commandButton(final int buttonPositionX, final int buttonPositionY, final Texture icon,
			final int abilityHandleId, final int orderId, final int autoCastId, final boolean active,
			final boolean autoCastActive, final boolean menuButton) {
		int x = Math.max(0, Math.min(COMMAND_CARD_WIDTH - 1, buttonPositionX));
		int y = Math.max(0, Math.min(COMMAND_CARD_HEIGHT - 1, buttonPositionY));
		while ((x < COMMAND_CARD_WIDTH) && (y < COMMAND_CARD_HEIGHT) && this.commandCard[y][x].isVisible()) {
			x++;
			if (x >= COMMAND_CARD_WIDTH) {
				x = 0;
				y++;
			}
		}
		if ((x < COMMAND_CARD_WIDTH) && (y < COMMAND_CARD_HEIGHT)) {
			this.commandCard[y][x].setCommandButtonData(icon, abilityHandleId, orderId, autoCastId, active,
					autoCastActive, menuButton);
		}
	}

	public void resize(final Rectangle viewport) {
		this.cameraManager.resize(viewport);
		positionPortrait();
	}

	public void positionPortrait() {
		this.projectionTemp1.x = 422 * this.widthRatioCorrection;
		this.projectionTemp1.y = 57 * this.heightRatioCorrection;
		this.projectionTemp2.x = (422 + 167) * this.widthRatioCorrection;
		this.projectionTemp2.y = (57 + 170) * this.heightRatioCorrection;
		this.uiViewport.project(this.projectionTemp1);
		this.uiViewport.project(this.projectionTemp2);

		this.tempRect.x = this.projectionTemp1.x + this.uiViewport.getScreenX();
		this.tempRect.y = this.projectionTemp1.y + this.uiViewport.getScreenY();
		this.tempRect.width = this.projectionTemp2.x - this.projectionTemp1.x;
		this.tempRect.height = this.projectionTemp2.y - this.projectionTemp1.y;
		this.portrait.portraitScene.camera.viewport(this.tempRect);
	}

	private static final class InfoPanelIconBackdrops {
		private final Texture[] damageBackdropTextures;

		public InfoPanelIconBackdrops(final CodeKeyType[] attackTypes, final GameUI gameUI, final String prefix,
				final String suffix) {
			this.damageBackdropTextures = new Texture[attackTypes.length];
			for (int index = 0; index < attackTypes.length; index++) {
				final CodeKeyType attackType = attackTypes[index];
				String skinLookupKey = "InfoPanelIcon" + prefix + attackType.getCodeKey() + suffix;
				final Texture suffixTexture = gameUI.loadTexture(gameUI.getSkinField(skinLookupKey));
				if (suffixTexture != null) {
					this.damageBackdropTextures[index] = suffixTexture;
				}
				else {
					skinLookupKey = "InfoPanelIcon" + prefix + attackType.getCodeKey();
					this.damageBackdropTextures[index] = gameUI.loadTexture(gameUI.getSkinField(skinLookupKey));
				}
			}
		}

		public Texture getTexture(final CodeKeyType attackType) {
			if (attackType != null) {
				final int ordinal = attackType.ordinal();
				if ((ordinal >= 0) && (ordinal < this.damageBackdropTextures.length)) {
					return this.damageBackdropTextures[ordinal];
				}
			}
			return this.damageBackdropTextures[0];
		}

		private static String getSuffix(final CAttackType attackType) {
			switch (attackType) {
			case CHAOS:
				return "Chaos";
			case HERO:
				return "Hero";
			case MAGIC:
				return "Magic";
			case NORMAL:
				return "Normal";
			case PIERCE:
				return "Pierce";
			case SIEGE:
				return "Siege";
			case SPELLS:
				return "Magic";
			case UNKNOWN:
				return "Unknown";
			default:
				throw new IllegalArgumentException("Unknown attack type: " + attackType);
			}

		}
	}

	@Override
	public void lifeChanged() {
		if (this.selectedUnit.getSimulationUnit().isDead()) {
			selectUnit(null);
		}
		else {
			this.unitLifeText
					.setText(FastNumberFormat.formatWholeNumber(this.selectedUnit.getSimulationUnit().getLife()) + " / "
							+ this.selectedUnit.getSimulationUnit().getMaximumLife());
		}
	}

	@Override
	public void goldChanged() {
		this.resourceBarGoldText.setText(Integer.toString(this.localPlayer.getGold()));
	}

	@Override
	public void lumberChanged() {
		this.resourceBarLumberText.setText(Integer.toString(this.localPlayer.getLumber()));
	}

	@Override
	public void foodChanged() {
		final int foodCap = this.localPlayer.getFoodCap();
		if (foodCap == 0) {
			this.resourceBarSupplyText.setText(Integer.toString(this.localPlayer.getFoodUsed()));
			this.resourceBarSupplyText.setColor(Color.WHITE);
		}
		else {
			this.resourceBarSupplyText.setText(this.localPlayer.getFoodUsed() + "/" + foodCap);
			this.resourceBarSupplyText.setColor(this.localPlayer.getFoodUsed() > foodCap ? Color.RED : Color.WHITE);
		}
	}

	@Override
	public void upkeepChanged() {
		this.resourceBarUpkeepText.setText("Upkeep NYI");
		this.resourceBarUpkeepText.setColor(Color.CYAN);
	}

	@Override
	public void ordersChanged() {
		reloadSelectedUnitUI(this.selectedUnit);
	}

	@Override
	public void queueChanged() {
		reloadSelectedUnitUI(this.selectedUnit);
	}

	private void clearAndRepopulateCommandCard() {
		clearCommandCard();
		final AbilityDataUI abilityDataUI = this.war3MapViewer.getAbilityDataUI();
		final int menuOrderId = getSubMenuOrderId();
		if (this.activeCommand != null) {
			final IconUI cancelUI = abilityDataUI.getCancelUI();
			this.commandButton(cancelUI.getButtonPositionX(), cancelUI.getButtonPositionY(), cancelUI.getIcon(), 0,
					menuOrderId, 0, false, false, true);
		}
		else {
			if (menuOrderId != 0) {
				final int exitOrderId = this.subMenuOrderIdStack.size() > 1
						? this.subMenuOrderIdStack.get(this.subMenuOrderIdStack.size() - 2)
						: 0;
				final IconUI cancelUI = abilityDataUI.getCancelUI();
				this.commandButton(cancelUI.getButtonPositionX(), cancelUI.getButtonPositionY(), cancelUI.getIcon(), 0,
						exitOrderId, 0, false, false, true);
			}
			this.selectedUnit.populateCommandCard(this.war3MapViewer.simulation, this, abilityDataUI, menuOrderId);
		}
	}

	private int getSubMenuOrderId() {
		return this.subMenuOrderIdStack.isEmpty() ? 0
				: this.subMenuOrderIdStack.get(this.subMenuOrderIdStack.size() - 1);
	}

	public RenderUnit getSelectedUnit() {
		return this.selectedUnit;
	}

	public boolean keyDown(final int keycode) {
		return this.cameraManager.keyDown(keycode);
	}

	public boolean keyUp(final int keycode) {
		return this.cameraManager.keyUp(keycode);
	}

	public void scrolled(final int amount) {
		this.cameraManager.scrolled(amount);
	}

	public boolean touchDown(final int screenX, final int screenY, final float worldScreenY, final int button) {
		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);
		if (this.meleeUIMinimap.containsMouse(screenCoordsVector.x, screenCoordsVector.y)) {
			final Vector2 worldPoint = this.meleeUIMinimap.getWorldPointFromScreen(screenCoordsVector.x,
					screenCoordsVector.y);
			this.cameraManager.target.x = worldPoint.x;
			this.cameraManager.target.y = worldPoint.y;
			return true;
		}
		final UIFrame clickedUIFrame = this.rootFrame.touchDown(screenCoordsVector.x, screenCoordsVector.y, button);
		if (clickedUIFrame == null) {
			// try to interact with world
			if (this.activeCommand != null) {
				if (button == Input.Buttons.RIGHT) {
					this.activeCommandUnit = null;
					this.activeCommand = null;
					this.activeCommandOrderId = -1;
					clearAndRepopulateCommandCard();
				}
				else {
					final RenderUnit rayPickUnit = this.war3MapViewer.rayPickUnit(screenX, worldScreenY,
							this.activeCommandUnitTargetFilter);
					final boolean shiftDown = isShiftDown();
					if (rayPickUnit != null) {
						this.unitOrderListener.issueTargetOrder(
								this.activeCommandUnit.getSimulationUnit().getHandleId(),
								this.activeCommand.getHandleId(), this.activeCommandOrderId,
								rayPickUnit.getSimulationUnit().getHandleId(), shiftDown);
						if (getSelectedUnit().soundset.yesAttack
								.playUnitResponse(this.war3MapViewer.worldScene.audioContext, getSelectedUnit())) {
							portraitTalk();
						}
						this.selectedSoundCount = 0;
						if (this.activeCommand instanceof CAbilityRally) {
							this.war3MapViewer.getUiSounds().getSound("RallyPointPlace").play(this.uiScene.audioContext,
									0, 0);
						}
						if (!shiftDown) {
							this.subMenuOrderIdStack.clear();
							this.activeCommandUnit = null;
							this.activeCommand = null;
							this.activeCommandOrderId = -1;
							clearAndRepopulateCommandCard();
						}
					}
					else {
						this.war3MapViewer.getClickLocation(clickLocationTemp, screenX, (int) worldScreenY);
						clickLocationTemp2.set(clickLocationTemp.x, clickLocationTemp.y);

						this.activeCommand.checkCanTarget(this.war3MapViewer.simulation,
								this.activeCommandUnit.getSimulationUnit(), this.activeCommandOrderId,
								clickLocationTemp2, PointAbilityTargetCheckReceiver.INSTANCE);
						final Vector2 target = PointAbilityTargetCheckReceiver.INSTANCE.getTarget();
						if (target != null) {
							if ((this.activeCommand instanceof CAbilityAttack)
									&& (this.activeCommandOrderId == OrderIds.attack)) {
								this.war3MapViewer.showConfirmation(clickLocationTemp, 1, 0, 0);
							}
							else {
								this.war3MapViewer.showConfirmation(clickLocationTemp, 0, 1, 0);
							}
							this.unitOrderListener.issuePointOrder(
									this.activeCommandUnit.getSimulationUnit().getHandleId(),
									this.activeCommand.getHandleId(), this.activeCommandOrderId, clickLocationTemp2.x,
									clickLocationTemp2.y, shiftDown);
							if (getSelectedUnit().soundset.yes
									.playUnitResponse(this.war3MapViewer.worldScene.audioContext, getSelectedUnit())) {
								portraitTalk();
							}
							this.selectedSoundCount = 0;
							if (this.activeCommand instanceof AbstractCAbilityBuild) {
								this.war3MapViewer.getUiSounds().getSound("PlaceBuildingDefault")
										.play(this.uiScene.audioContext, 0, 0);
							}
							else if (this.activeCommand instanceof CAbilityRally) {
								this.war3MapViewer.getUiSounds().getSound("RallyPointPlace")
										.play(this.uiScene.audioContext, 0, 0);
							}
							if (!shiftDown) {
								this.subMenuOrderIdStack.clear();
								this.activeCommandUnit = null;
								this.activeCommand = null;
								this.activeCommandOrderId = -1;
								clearAndRepopulateCommandCard();
							}

						}
					}
				}
			}
			else {
				if (button == Input.Buttons.RIGHT) {
					if (getSelectedUnit() != null) {
						final RenderUnit rayPickUnit = this.war3MapViewer.rayPickUnit(screenX, worldScreenY);
						if ((rayPickUnit != null) && !rayPickUnit.getSimulationUnit().isDead()) {
							boolean ordered = false;
							boolean rallied = false;
							for (final RenderUnit unit : this.war3MapViewer.selected) {
								for (final CAbility ability : unit.getSimulationUnit().getAbilities()) {
									ability.checkCanTarget(this.war3MapViewer.simulation, unit.getSimulationUnit(),
											OrderIds.smart, rayPickUnit.getSimulationUnit(),
											CWidgetAbilityTargetCheckReceiver.INSTANCE);
									final CWidget targetWidget = CWidgetAbilityTargetCheckReceiver.INSTANCE.getTarget();
									if (targetWidget != null) {
										this.unitOrderListener.issueTargetOrder(unit.getSimulationUnit().getHandleId(),
												ability.getHandleId(), OrderIds.smart, targetWidget.getHandleId(),
												isShiftDown());
										rallied |= ability instanceof CAbilityRally;
										ordered = true;
									}
								}

							}
							if (ordered) {
								if (getSelectedUnit().soundset.yesAttack.playUnitResponse(
										this.war3MapViewer.worldScene.audioContext, getSelectedUnit())) {
									portraitTalk();
								}
								if (rallied) {
									this.war3MapViewer.getUiSounds().getSound("RallyPointPlace")
											.play(this.uiScene.audioContext, 0, 0);
								}
								this.selectedSoundCount = 0;
							}
						}
						else {
							this.war3MapViewer.getClickLocation(clickLocationTemp, screenX, (int) worldScreenY);
							this.war3MapViewer.showConfirmation(clickLocationTemp, 0, 1, 0);
							clickLocationTemp2.set(clickLocationTemp.x, clickLocationTemp.y);

							boolean ordered = false;
							boolean rallied = false;
							for (final RenderUnit unit : this.war3MapViewer.selected) {
								for (final CAbility ability : unit.getSimulationUnit().getAbilities()) {
									ability.checkCanUse(this.war3MapViewer.simulation, unit.getSimulationUnit(),
											OrderIds.smart, BooleanAbilityActivationReceiver.INSTANCE);
									if (BooleanAbilityActivationReceiver.INSTANCE.isOk()) {
										ability.checkCanTarget(this.war3MapViewer.simulation, unit.getSimulationUnit(),
												OrderIds.smart, clickLocationTemp2,
												PointAbilityTargetCheckReceiver.INSTANCE);
										final Vector2 target = PointAbilityTargetCheckReceiver.INSTANCE.getTarget();
										if (target != null) {
											this.unitOrderListener.issuePointOrder(
													unit.getSimulationUnit().getHandleId(), ability.getHandleId(),
													OrderIds.smart, clickLocationTemp2.x, clickLocationTemp2.y,
													isShiftDown());
											rallied |= ability instanceof CAbilityRally;
											ordered = true;
										}
									}
								}

							}

							if (ordered) {
								if (getSelectedUnit().soundset.yes.playUnitResponse(
										this.war3MapViewer.worldScene.audioContext, getSelectedUnit())) {
									portraitTalk();
								}
								if (rallied) {
									this.war3MapViewer.getUiSounds().getSound("RallyPointPlace")
											.play(this.uiScene.audioContext, 0, 0);
								}
								this.selectedSoundCount = 0;
							}
						}
					}
				}
				else {
					final List<RenderUnit> selectedUnits = this.war3MapViewer.selectUnit(screenX, worldScreenY, false);
					selectUnits(selectedUnits);
				}
			}
		}
		else {
			if (clickedUIFrame instanceof ClickableActionFrame) {
				this.mouseDownUIFrame = (ClickableActionFrame) clickedUIFrame;
				this.mouseDownUIFrame.mouseDown(this.rootFrame, this.uiViewport);
			}
		}
		return false;
	}

	private void selectUnits(final List<RenderUnit> selectedUnits) {
		if (!selectedUnits.isEmpty()) {
			final RenderUnit unit = selectedUnits.get(0);
			final boolean selectionChanged = getSelectedUnit() != unit;
			boolean playedNewSound = false;
			if (selectionChanged) {
				this.selectedSoundCount = 0;
			}
			if (unit.soundset != null) {
				UnitSound ackSoundToPlay = unit.soundset.what;
				int soundIndex;
				final int pissedSoundCount = unit.soundset.pissed.getSoundCount();
				if (unit.getSimulationUnit().isConstructing()) {
					ackSoundToPlay = this.war3MapViewer.getUiSounds()
							.getSound(this.rootFrame.getSkinField("ConstructingBuilding"));
					soundIndex = (int) (Math.random() * ackSoundToPlay.getSoundCount());
				}
				else {
					if ((this.selectedSoundCount >= 3) && (pissedSoundCount > 0)) {
						soundIndex = this.selectedSoundCount - 3;
						ackSoundToPlay = unit.soundset.pissed;
					}
					else {
						soundIndex = (int) (Math.random() * ackSoundToPlay.getSoundCount());
					}
				}
				if ((ackSoundToPlay != null) && ackSoundToPlay
						.playUnitResponse(this.war3MapViewer.worldScene.audioContext, unit, soundIndex)) {
					this.selectedSoundCount++;
					if ((this.selectedSoundCount - 3) >= pissedSoundCount) {
						this.selectedSoundCount = 0;
					}
					playedNewSound = true;
				}
			}
			if (selectionChanged) {
				selectUnit(unit);
			}
			if (playedNewSound) {
				portraitTalk();
			}
		}
		else {
			selectUnit(null);
		}
	}

	public boolean touchUp(final int screenX, final int screenY, final float worldScreenY, final int button) {
		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);
		final UIFrame clickedUIFrame = this.rootFrame.touchUp(screenCoordsVector.x, screenCoordsVector.y, button);
		if (this.mouseDownUIFrame != null) {
			if (clickedUIFrame == this.mouseDownUIFrame) {
				this.mouseDownUIFrame.onClick(button);
				this.war3MapViewer.getUiSounds().getSound("InterfaceClick").play(this.uiScene.audioContext, 0, 0);
			}
			this.mouseDownUIFrame.mouseUp(this.rootFrame, this.uiViewport);
		}
		this.mouseDownUIFrame = null;
		return false;
	}

	private static boolean isShiftDown() {
		return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
	}

	public boolean touchDragged(final int screenX, final int screenY, final float worldScreenY, final int pointer) {
		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);

		if (this.meleeUIMinimap.containsMouse(screenCoordsVector.x, screenCoordsVector.y)) {
			final Vector2 worldPoint = this.meleeUIMinimap.getWorldPointFromScreen(screenCoordsVector.x,
					screenCoordsVector.y);
			this.cameraManager.target.x = worldPoint.x;
			this.cameraManager.target.y = worldPoint.y;
		}
		return false;
	}

	public boolean mouseMoved(final int screenX, final int screenY, final float worldScreenY) {
		screenCoordsVector.set(screenX, screenY);
		this.uiViewport.unproject(screenCoordsVector);
		final UIFrame mousedUIFrame = this.rootFrame.getFrameChildUnderMouse(screenCoordsVector.x,
				screenCoordsVector.y);
		if (mousedUIFrame != this.mouseOverUIFrame) {
			if (mousedUIFrame instanceof ClickableActionFrame) {
				this.mouseOverUIFrame = (ClickableActionFrame) mousedUIFrame;
				final String toolTip = this.mouseOverUIFrame.getToolTip();
				this.tooltipText.setText(toolTip);
				System.out.println("tooltip text assign to " + toolTip);
				this.tooltipFrame.setHeight(GameUI.convertY(this.uiViewport, 0.020f));
				this.tooltipFrame.positionBounds(this.rootFrame, this.uiViewport);
				this.tooltipFrame.setVisible(true);
			}
			else {
				this.mouseOverUIFrame = null;
				this.tooltipFrame.setVisible(false);
			}
		}
		return false;
	}

	public float getHeightRatioCorrection() {
		return this.heightRatioCorrection;
	}

	@Override
	public void queueIconClicked(final int index) {
		final CUnit simulationUnit = this.selectedUnit.getSimulationUnit();
		if (simulationUnit.isConstructing()) {
			switch (index) {
			case 0:
				for (final CAbility ability : simulationUnit.getAbilities()) {
					ability.checkCanUse(this.war3MapViewer.simulation, simulationUnit, OrderIds.cancel,
							BooleanAbilityActivationReceiver.INSTANCE);
					if (BooleanAbilityActivationReceiver.INSTANCE.isOk()) {

						final BooleanAbilityTargetCheckReceiver<Void> targetCheckReceiver = BooleanAbilityTargetCheckReceiver
								.<Void>getInstance().reset();
						ability.checkCanTargetNoTarget(this.war3MapViewer.simulation, simulationUnit, OrderIds.cancel,
								targetCheckReceiver);
						if (targetCheckReceiver.isTargetable()) {
							this.unitOrderListener.issueImmediateOrder(simulationUnit.getHandleId(),
									ability.getHandleId(), OrderIds.cancel, false);
						}
					}
				}
				break;
			case 1:
				final List<RenderUnit> unitList = Arrays.asList(
						this.war3MapViewer.getRenderPeer(this.selectedUnit.getSimulationUnit().getWorkerInside()));
				this.war3MapViewer.doSelectUnit(unitList);
				selectUnits(unitList);
				break;
			}
		}
		else {
			this.unitOrderListener.unitCancelTrainingItem(simulationUnit.getHandleId(), index);
		}
	}
}
