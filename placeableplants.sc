__config() -> {'stay_loaded' -> true, 'scope' -> 'global'};

global_placeable_items = {'allium', 'azure_bluet', 'bamboo', 'big_dripleaf', 'blue_orchid', 'cornflower', 'crimson_fungus', 'crimson_roots', 'dandelion', 'dead_bush', 'fern', 'grass', 'lily_of_the_valley', 'nether_sprouts', 'orange_tulip', 'oxeye_daisy', 'pink_tulip', 'poppy', 'red_tulip', 'sweet_berries', 'warped_fungus', 'warped_roots', 'white_tulip', 'wither_rose', 'mangrove_propagule'};
global_placeable_tall_items = {'large_fern', 'lilac', 'peony', 'rose_bush', 'sunflower', 'tall_grass'};

// script run l=[];for(block_list(),set([x,y,z],'air');set([x,y-1,z],_);if(place_item('rail',[x,y,z]), l+=_));print(map(l,'\''+_+'\''))
// += 'dirt_path', 'oak_leaves', 'spruce_leaves', 'birch_leaves', 'jungle_leaves', 'acacia_leaves', 'dark_oak_leaves', 'azalea_leaves', 'flowering_azalea_leaves'
// -= 'grass_block', 'dirt', 'coarse_dirt', 'podzol', 'mycelium', 'rooted_dirt', 'moss_block', 'mud', 'muddy_mangrove_roots'
global_flat_surface = {'structure_block', 'basalt', 'chiseled_deepslate', 'red_wool', 'stripped_dark_oak_wood', 'nether_gold_ore', 'bone_block', 'black_concrete', 'melon', 'cracked_deepslate_tiles', 'deepslate_gold_ore', 'red_mushroom_block', 'nether_wart_block', 'green_concrete', 'magenta_wool', 'coal_block', 'black_wool', 'iron_block', 'beehive', 'blackstone', 'lapis_ore', 'gravel', 'birch_wood', 'oak_wood', 'scaffolding', 'emerald_block', 'chiseled_nether_bricks', 'copper_block', 'purple_wool', 'spruce_wood', 'green_stained_glass', 'diamond_ore', 'pink_glazed_terracotta', 'pink_terracotta', 'black_shulker_box', 'orange_stained_glass', 'magenta_concrete', 'mangrove_wood', 'redstone_ore', 'polished_basalt', 'cyan_concrete_powder', 'sand', 'acacia_log', 'light_blue_concrete', 'sticky_piston', 'gray_concrete', 'blue_glazed_terracotta', 'jigsaw', 'stripped_spruce_log', 'green_concrete_powder', 'cracked_deepslate_bricks', 'brown_stained_glass', 'pink_shulker_box', 'tinted_glass', 'blue_terracotta', 'stripped_oak_wood', 'sandstone', 'yellow_concrete_powder', 'gray_terracotta', 'dead_brain_coral_block', 'lodestone', 'deepslate_copper_ore', 'red_concrete_powder', 'gray_shulker_box', 'purpur_block', 'glass', 'light_gray_shulker_box', 'dried_kelp_block', 'terracotta', 'budding_amethyst', 'stripped_crimson_hyphae', 'warped_nylium', 'infested_chiseled_stone_bricks', 'gray_concrete_powder', 'light_blue_concrete_powder', 'chain_command_block', 'oak_log', 'cyan_terracotta', 'cut_red_sandstone', 'warped_planks', 'dead_horn_coral_block', 'mushroom_stem', 'beacon', 'tnt', 'deepslate_lapis_ore', 'prismarine_bricks', 'cyan_shulker_box', 'nether_bricks', 'stone_bricks', 'infested_stone_bricks', 'light_gray_stained_glass', 'infested_cracked_stone_bricks', 'brown_concrete_powder', 'cyan_stained_glass', 'cyan_glazed_terracotta', 'pearlescent_froglight', 'diorite', 'crimson_hyphae', 'bedrock', 'dripstone_block', 'jungle_planks', 'yellow_wool', 'lime_shulker_box', 'blue_ice', 'red_nether_bricks', 'crying_obsidian', 'prismarine', 'crimson_stem', 'purple_stained_glass', 'chiseled_sandstone', 'clay', 'dead_bubble_coral_block', 'target', 'gray_wool', 'white_concrete_powder', 'wet_sponge', 'yellow_terracotta', 'waxed_weathered_cut_copper', 'yellow_concrete', 'stripped_birch_log', 'white_terracotta', 'lime_concrete_powder', 'waxed_copper_block', 'barrel', 'sculk', 'warped_hyphae', 'magenta_glazed_terracotta', 'chiseled_stone_bricks', 'smithing_table', 'magma_block', 'purple_terracotta', 'cauldron', 'pink_concrete_powder', 'bee_nest', 'exposed_copper', 'magenta_concrete_powder', 'hay_block', 'purple_shulker_box', 'weathered_copper', 'red_sand', 'cracked_polished_blackstone_bricks', 'gray_stained_glass', 'white_concrete', 'iron_ore', 'reinforced_deepslate', 'stripped_dark_oak_log', 'red_terracotta', 'calcite', 'cut_copper', 'stripped_mangrove_log', 'deepslate_emerald_ore', 'brown_glazed_terracotta', 'smooth_basalt', 'brown_concrete', 'white_wool', 'blue_concrete_powder', 'blue_stained_glass', 'deepslate_coal_ore', 'smooth_red_sandstone', 'crimson_planks', 'waxed_oxidized_copper', 'netherite_block', 'deepslate', 'light_blue_terracotta', 'mangrove_planks', 'yellow_shulker_box', 'horn_coral_block', 'stripped_oak_log', 'ancient_debris', 'black_concrete_powder', 'infested_stone', 'purple_concrete_powder', 'birch_planks', 'green_terracotta', 'white_stained_glass', 'acacia_planks', 'slime_block', 'quartz_pillar', 'lime_concrete', 'mud_bricks', 'stripped_warped_stem', 'soul_sand', 'brown_terracotta', 'blue_wool', 'note_block', 'red_shulker_box', 'piston', 'dead_fire_coral_block', 'bubble_coral_block', 'smooth_quartz', 'smooth_sandstone', 'light_blue_glazed_terracotta', 'shulker_box', 'dispenser', 'sponge', 'purple_glazed_terracotta', 'spruce_planks', 'mangrove_roots', 'red_stained_glass', 'deepslate_iron_ore', 'mossy_stone_bricks', 'polished_blackstone_bricks', 'cracked_stone_bricks', 'jack_o_lantern', 'hopper', 'magenta_stained_glass', 'green_glazed_terracotta', 'cobblestone','lime_terracotta', 'jungle_wood', 'waxed_oxidized_cut_copper', 'cartography_table', 'polished_diorite', 'crimson_nylium', 'blast_furnace', 'stripped_jungle_log', 'infested_deepslate', 'lime_wool', 'light_gray_wool', 'warped_stem', 'waxed_cut_copper', 'stripped_crimson_stem', 'chiseled_polished_blackstone', 'brain_coral_block', 'fletching_table', 'powder_snow_cauldron', 'purpur_pillar', 'pink_stained_glass', 'cyan_concrete', 'stripped_birch_wood', 'sculk_catalyst', 'dropper', 'redstone_lamp', 'mossy_cobblestone', 'frosted_ice', 'polished_deepslate', 'brown_shulker_box', 'azalea', 'nether_quartz_ore', 'stripped_acacia_log', 'waxed_exposed_copper', 'blue_concrete', 'spawner', 'waxed_weathered_copper', 'deepslate_redstone_ore', 'quartz_bricks', 'crafting_table', 'purple_concrete', 'dark_oak_wood', 'light_blue_wool', 'infested_mossy_stone_bricks', 'light_gray_concrete', 'orange_wool', 'orange_glazed_terracotta', 'gray_glazed_terracotta', 'deepslate_bricks', 'raw_gold_block', 'red_concrete', 'stripped_acacia_wood', 'amethyst_block', 'black_glazed_terracotta', 'ochre_froglight', 'brown_wool', 'raw_copper_block', 'orange_concrete', 'warped_wart_block', 'dark_prismarine', 'pumpkin', 'raw_iron_block', 'dark_oak_planks', 'orange_concrete_powder', 'redstone_block', 'honeycomb_block', 'quartz_block', 'cobbled_deepslate', 'waxed_exposed_cut_copper',  'pink_concrete', 'water_cauldron', 'loom', 'birch_log', 'smoker', 'green_shulker_box', 'ice', 'granite', 'bricks', 'carved_pumpkin', 'green_wool', 'yellow_glazed_terracotta', 'spruce_log', 'andesite', 'blue_shulker_box', 'exposed_cut_copper', 'polished_blackstone', 'netherrack', 'tuff', 'emerald_ore', 'packed_ice', 'stone', 'diamond_block', 'mangrove_log', 'tube_coral_block', 'yellow_stained_glass', 'light_blue_stained_glass', 'glowstone', 'respawn_anchor', 'chiseled_red_sandstone', 'dark_oak_log', 'smooth_stone', 'bookshelf', 'copper_ore', 'lapis_block', 'fire_coral_block', 'black_terracotta', 'magenta_shulker_box', 'snow_block', 'cyan_wool', 'composter', 'jukebox', 'light_gray_terracotta', 'sea_lantern', 'light_blue_shulker_box', 'obsidian', 'light_gray_glazed_terracotta', 'magenta_terracotta', 'stripped_mangrove_wood', 'chorus_flower', 'weathered_cut_copper', 'polished_granite', 'oxidized_cut_copper', 'chiseled_quartz_block', 'stripped_warped_hyphae', 'brown_mushroom_block', 'pink_wool', 'end_stone_bricks', 'oxidized_copper', 'end_stone', 'light_gray_concrete_powder', 'furnace', 'cracked_nether_bricks', 'command_block', 'dead_tube_coral_block', 'repeating_command_block', 'oak_planks', 'white_shulker_box', 'soul_soil', 'gilded_blackstone', 'red_sandstone', 'cut_sandstone', 'verdant_froglight', 'shroomlight', 'flowering_azalea', 'infested_cobblestone', 'gold_ore', 'orange_terracotta', 'deepslate_tiles', 'white_glazed_terracotta', 'red_glazed_terracotta', 'gold_block', 'deepslate_diamond_ore', 'observer', 'lime_glazed_terracotta', 'jungle_log', 'packed_mud', 'barrier', 'lime_stained_glass', 'orange_shulker_box', 'stripped_spruce_wood', 'acacia_wood', 'stripped_jungle_wood', 'coal_ore', 'lava_cauldron', 'polished_andesite', 'black_stained_glass', 'dirt_path', 'oak_leaves', 'spruce_leaves', 'birch_leaves', 'jungle_leaves', 'acacia_leaves', 'dark_oak_leaves', 'azalea_leaves', 'flowering_azalea_leaves'};
flat_surface(block) ->
    has(global_flat_surface, str(block)) || 
    block ~ '_trapdoor$' && block_state(block, 'half') == 'top' && block_state(block, 'open') == 'false' ||
    block ~ '_stairs$' && block_state(block, 'half') == 'top' ||
    block ~ '_slab$' && (block_state(block, 'type') == 'top' || block_state(block, 'type') == 'double');

_item_to_block(item) ->
if(
    item == 'sweet_berries', 'sweet_berry_bush',
    item
);

_block_sound(block) -> if(block == 'sweet_berry_bush', 'sweet_berry_bush',
                          block == 'big_dripleaf', 'big_dripleaf',
                          block_sound(block));


_placeable(player, item_tuple, hand, block, face) -> (
    g = player ~ 'gamemode';
    if(g == 'spectator' || !item_tuple, return());
    [item, count, nbt] = item_tuple;
    if(item == 'wither_rose' && block ~ 'nether_brick', return());
    if(face != 'up' || !has(global_placeable_items, item) && !has(global_placeable_tall_items, item) || !flat_surface(block), return());
    if(g=='adventure' && !nbt:'CanPlaceOn' ~ str('"minecraft:%s"',block), return());
    b1 = pos_offset(block, face);
    if(!air(b1), return());
    without_updates(
        if(
            has(global_placeable_tall_items, item),
                b2 = pos_offset(b1, face);
                if(!air(b2), return());
                set(b1, item, 'half', 'lower');
                set(b2, item, 'half', 'upper'),
            set(b1, _item_to_block(item))
        )
    );
    modify(player, 'swing', hand);
    sound(str('block.%s.place', _block_sound(block(b1))), b1, 1, 1, 'block');
    if(g == 'creative', return()); 
    inventory_set(player, if(hand=='mainhand',player~'selected_slot',-1), count - 1, item ,nbt)
);

__on_player_right_clicks_block(player, item_tuple, hand, block, face, hitvec) -> (
    _placeable(player, item_tuple, hand, block, face)
)
