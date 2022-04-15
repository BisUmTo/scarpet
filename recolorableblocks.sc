//////
// recolorableblocks by BisUmTo
// Allows to craft colored blocks starting from already colored ones
//////

__config() -> {
    'scope' -> 'global'
};

global_app_name = system_info('app_name');

default_colors = {
    'black',
    'red',
    'green',
    'brown',
    'blue',
    'purple',
    'cyan',
    'light_gray',
    'gray',
    'pink',
    'lime',
    'yellow',
    'light_blue',
    'magenta',
    'orange',
    'white'
};

coral_colors = {
    'brain' -> 'pink',
    'bubble' -> 'magenta',
    'fire' -> 'red',
    'horn' -> 'yellow',
    'tube' -> 'blue',
};

global_categories = {
    'banner' -> {
        'ratio' -> 8, 
        'colors' -> default_colors
    },
    'bed' -> {
        'ratio' -> 1, 
        'colors' -> default_colors,
        'override_suffix' -> '_from_white_bed',
        'override' -> true,
        'item_category' -> 'decorations'
    },
    'candle' -> {
        'ratio' -> 8, 
        'colors' -> default_colors,
        'default' -> 'candle',
        'override' -> true,
        'item_category' -> 'decorations'
    },
    'carpet' -> {
        'ratio' -> 8, 
        'colors' -> default_colors,
        'override_suffix' -> '_from_white_carpet',
        'override' -> true,
        'item_category' -> 'decorations'
    },
    'concrete' -> {
        'ratio' -> 8, 
        'colors' -> default_colors,
        'override' -> true,
        'item_category' -> 'building_blocks'
    },
    'concrete_powder' -> {
        'ratio' -> 8, 
        'colors' -> default_colors,
        'override' -> false,
        'item_category' -> 'building_blocks'
    },
    'coral' -> {
        'ratio' -> 8, 
        'colors' -> coral_colors,
        'prefixs' -> ['dead_']
    },
    'coral_block' -> {
        'ratio' -> 8, 
        'colors' -> coral_colors,
        'prefixs' -> ['dead_']
    },
    'coral_fan' -> {
        'ratio' -> 8, 
        'colors' -> coral_colors,
        'prefixs' -> ['dead_']
    },
    'glazed_terracotta'  -> {
        'ratio' -> 8, 
        'colors' -> default_colors
    },
    'stained_glass' -> {
        'ratio' -> 8, 
        'colors' -> default_colors,
        'default' -> 'glass',
        'override' -> true,
        'item_category' -> 'building_blocks'
    },
    'stained_glass_pane' -> {
        'ratio' -> 8, 
        'colors' -> default_colors,
        'default' -> 'glass_pane',
        'override_suffix' -> '_from_glass_pane',
        'override' -> true,
        'item_category' -> 'decorations'
    },
    'terracotta' -> {
        'ratio' -> 8, 
        'colors' -> default_colors,
        'default' -> 'terracotta',
        'override' -> true,
        'item_category' -> 'building_blocks'
    },
    'wool' -> {
        'ratio' -> 1, 
        'colors' -> default_colors,
        'override' -> true,
        'item_category' -> 'building_blocks'
    },
};

item_tags = reduce(global_categories,
    category_prop = global_categories:(category = _);
    files = reduce(colors = category_prop:'colors',
        not_colors = copy(keys(colors));
        delete(not_colors, _i);
        _a:str('not_%s_%s.json', _, category) = {
            'values' -> [
                ...map(not_colors, str('minecraft:%s_%s',_,category)),
                ...if(default = category_prop:'default', 
                    [str('minecraft:%s',default)],
                    []
                )
            ]
        };
        _a
    , {});
    if(default = category_prop:'default',
        files:str('colored_%s.json', category) = {
            'values' -> map(category_prop:'colors', str('minecraft:%s_%s',_,category))
        };
    );
    _a:category = files;
    _a
, {});

recipes = reduce(global_categories,
    category_prop = global_categories:(category = _);
    files = reduce(colors = category_prop:'colors',
        _a:str('%s_%s.json', color = _, category) = {
            'type' -> 'crafting_shapeless',
            'ingredients' -> [
                {'item' -> str('minecraft:%s_dye', colors:color || color)},
                ... map(range(category_prop:'ratio'),
                    {'tag' -> str('%s:%s/not_%s_%s', global_app_name, category, color, category)}
                )
            ],
            'result' -> {
                'item' -> str('minecraft:%s_%s', color, category),
                'count' -> category_prop:'ratio'
            }
        };
        _a
    , {});
    if(default = category_prop:'default',
        files:str('%s.json', default) = {
            'type' -> 'crafting_shapeless',
            'ingredients' -> [
                {'tag' -> str('%s:%s/colored_%s', global_app_name, category, category)}
            ],
            'result' -> {
                'item' -> str('minecraft:%s', default)
            }
        };
    );
    _a:category = files;
    _a
, {});

advancements = reduce(global_categories,
    category_prop = global_categories:(category = _);
    files = reduce(colors = category_prop:'colors',
        _a:str('%s_%s.json', color = _, category) = {
            'parent' -> 'minecraft:recipes/root',
            'reward' -> {'recipes' -> [str('%s:%s/%s_%s', global_app_name, category, color, category)]},
            'criteria' -> {
                'has_item' -> {
                    'trigger' -> 'minecraft:inventory_changed',
                    'conditions' -> {'items' -> [
                        {'tag' -> str('%s:%s/not_%s_%s', global_app_name, category, color, category)}
                    ]}
                },
                'has_the_recipe' -> {
                    'trigger' -> 'minecraft:recipe_unlocked',
                    'conditions' -> {'recipe' -> str('%s:%s/%s_%s', global_app_name, category, color, category)}
                }
            },
            'requirements' -> [['has_item','has_the_recipe']]
        };
        _a
    , {});
    _a:category = files;
    _a
, {});

recipes_override = reduce(global_categories,
    category_prop = global_categories:(category = _);
    if(category_prop:'override',
        for(category_prop:'colors',
            _a:str('%s_%s%s.json', _, category, category_prop:'override_suffix'||'') = {
                'type' -> 'crafting_shapeless',
                'ingredients' -> [{'item' -> 'minecraft:structure_void'}],
                'result' -> {'item' -> 'minecraft:structure_void'},
            }
        )
    );
    _a
, {});

advancements_override = reduce(global_categories,
    category_prop = global_categories:(category = _);
    if(category_prop:'override' && category_prop:'item_category',
        item_category = _a:(category_prop:'item_category') || {};
        for(category_prop:'colors',
            item_category:str('%s_%s%s.json', _, category, category_prop:'override_suffix'||'') = {
                'criteria' -> {'impossible' -> {'trigger' -> 'minecraft:impossible'}}            
            }
        );
        _a:(category_prop:'item_category') = item_category
    );
    _a
, {});

create_datapack(global_app_name, structure = {
    'data' -> {
        global_app_name -> { 
            'tags' -> { 'items' -> item_tags },
            'advancements' -> {'recipes' -> advancements},
            'recipes' -> recipes
        },
        'minecraft' -> {
            'recipes' -> recipes_override,
            'advancements' -> {'recipes' -> advancements_override}
        }
    }
});
write_file('structure', 'json', structure);

run('datapack enable "file/'+global_app_name+'.zip"');
__on_close()-> (
    run('datapack disable "file/'+global_app_name+'.zip"')
)
