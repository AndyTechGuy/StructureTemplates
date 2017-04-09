/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.structureTemplates.internal.ui;

import com.google.common.collect.Lists;
import org.terasology.assets.management.AssetManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.rendering.nui.BaseInteractionScreen;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.databinding.Binding;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UIDropdownScrollable;
import org.terasology.structureTemplates.internal.components.WallAdderItemComponent;
import org.terasology.structureTemplates.internal.events.ReplaceBlocksRequest;
import org.terasology.world.block.BlockExplorer;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.BlockUri;

import java.util.List;
import java.util.Set;

/**
 *
 */
public class AddWallScreen extends BaseInteractionScreen {

    private UIDropdownScrollable<BlockUri> comboBox;
    private UIButton cancelButton;
    private UIButton placeWallButton;

    private Prefab selectedPrefab;

    @In
    private PrefabManager prefabManager;

    @In
    private LocalPlayer localPlayer;

    @In
    private AssetManager assetManager;

    @Override
    protected void initializeWithInteractionTarget(EntityRef interactionTarget) {

    }

    @Override
    public void initialise() {
        comboBox = find("comboBox", UIDropdownScrollable.class);
        BlockExplorer blockExplorer = new BlockExplorer(assetManager);
        Set<BlockUri> blocks = blockExplorer.getFreeformBlockFamilies();
        blocks.add(BlockManager.AIR_ID);
        List<BlockUri> blockList = Lists.newArrayList(blocks);
        blockList.sort((BlockUri o1, BlockUri o2) -> o1.toString().compareTo(o2.toString()));
        comboBox.setOptions(blockList);
        comboBox.bindSelection(new Binding<BlockUri>() {
            @Override
            public BlockUri get() {
                return getInteractionTarget().getComponent(WallAdderItemComponent.class).blockUri;
            }

            @Override
            public void set(BlockUri value) {
                getInteractionTarget().getComponent(WallAdderItemComponent.class).blockUri = value;
            }
        });

        cancelButton = find("cancelButton", UIButton.class);
        if (cancelButton != null) {
            cancelButton.subscribe(this::onCloseButton);
        }

        placeWallButton = find("placeWallButton", UIButton.class);
        if (placeWallButton != null) {
            placeWallButton.subscribe(this::onPlaceWallButton);
        }
    }

    private void onPlaceWallButton(UIWidget button) {
        getInteractionTarget().send(new ReplaceBlocksRequest());
        getManager().popScreen();
    }

    private void onCloseButton(UIWidget button) {
        getManager().popScreen();
    }


}
