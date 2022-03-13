//////
// playerheadrecipe by BisUmTo
// Adds crafting for player_head stating from another skull
//////

__config() -> {
    'scope' -> 'global'
};

global_app_name = system_info('app_name');

create_datapack(global_app_name, structure = {
    'data' -> {
        global_app_name -> {
            'tags' -> {'items' -> {
                'heads.json' -> {
                    'values' -> [
                        'minecraft:skeleton_skull',
                        'minecraft:wither_skeleton_skull',
                        'minecraft:player_head',
                        'minecraft:zombie_head',
                        'minecraft:creeper_head',
                        'minecraft:dragon_head'
                    ]
                }
            }},
            'advancements' -> {'recipes' -> {
                'repeater.json' -> {
                    'parent' -> 'minecraft:recipes/root',
                    'reward' -> {'recipes' -> [
                        str('%s:player_head', global_app_name)
                    ]},
                    'criteria' -> {
                        'has_item' -> {
                            'trigger' -> 'minecraft:inventory_changed',
                            'conditions' -> {'items' -> [
                                {'tag' -> str('%s:heads', global_app_name)}
                            ]}
                        },
                        'has_the_recipe' -> {
                            'trigger' -> 'minecraft:recipe_unlocked',
                            'conditions' -> {'recipe' -> str('%s:player_head', global_app_name)}
                        }
                    },
                    'requirements' -> [['has_item','has_the_recipe']]
                }
            }},
            'recipes' -> {
                'player_head.json' -> {
                    'type' -> 'minecraft:crafting_shapeless',
                    'group' ->  'dispenser',
                    'ingredients' ->  [
                        {'tag' -> str('%s:heads', global_app_name)}
                    ],
                    'result' ->  {
                        'item' ->  'minecraft:player_head'
                    }
                }
            }
        }
    }
});
write_file('structure', 'json', structure);

run('datapack enable "file/'+global_app_name+'.zip"');
__on_close()-> (
    run('datapack disable "file/'+global_app_name+'.zip"')
)
