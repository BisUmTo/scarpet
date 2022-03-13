//////
// fastredstonerecipes by BisUmTo
// Adds faster crafting for redstone components (dispensers and repeaters)
//////

__config() -> {
    'scope' -> 'global'
};

global_app_name = system_info('app_name');

create_datapack(global_app_name, structure = {
    'data' -> {
        global_app_name -> { 
            'advancements' -> {'recipes' -> {
                'repeater.json' -> {
                    'parent' -> 'minecraft:recipes/root',
                    'reward' -> {'recipes' -> [
                        str('%s:repeater', global_app_name)
                    ]},
                    'criteria' -> {
                        'has_item' -> {
                            'trigger' -> 'minecraft:inventory_changed',
                            'conditions' -> {'items' -> [
                                {'item' -> 'minecraft:redstone'}
                            ]}
                        },
                        'has_the_recipe' -> {
                            'trigger' -> 'minecraft:recipe_unlocked',
                            'conditions' -> {'recipe' -> str('%s:repeater', global_app_name)}
                        }
                    },
                    'requirements' -> [['has_item','has_the_recipe']]
                },
                'dispenser.json' -> {
                    'parent' -> 'minecraft:recipes/root',
                    'reward' -> {'recipes' -> [
                        str('%s:dispenser1', global_app_name),
                        str('%s:dispenser2', global_app_name),
                    ]},
                    'criteria' -> {
                        'has_item' -> {
                            'trigger' -> 'minecraft:inventory_changed',
                            'conditions' -> {'items' -> [
                                {'item' -> 'minecraft:dropper'}
                            ]}
                        },
                        'has_the_recipe1' -> {
                            'trigger' -> 'minecraft:recipe_unlocked',
                            'conditions' -> {'recipe' -> str('%s:dispenser1', global_app_name)}
                        },
                        'has_the_recipe2' -> {
                            'trigger' -> 'minecraft:recipe_unlocked',
                            'conditions' -> {'recipe' -> str('%s:dispenser2', global_app_name)}
                        }
                    },
                    'requirements' -> [['has_item','has_the_recipe1','has_the_recipe2']]
                }
            }},
            'recipes' -> {
                'repeater.json' -> {
                    'type' -> 'crafting_shaped',
                    'group' ->  'repeater',
                    'pattern' ->  [
                        '# #',
                        'X#X',
                        'III'
                    ],
                    'key' ->  {
                        '#' -> {'item' -> 'minecraft:redstone'},
                        'X' -> {'item' -> 'minecraft:stick'},
                        'I' -> {'item' -> 'minecraft:stone'}
                    },
                    'result' ->  {
                        'item' ->  'minecraft:repeater'
                    }
                },
                'dispenser1.json' -> {
                    'type' -> 'crafting_shaped',
                    'group' ->  'dispenser',
                    'pattern' ->  [
                        '#I ',
                        '#XI',
                        '#I '
                    ],
                    'key' ->  {
                        '#' -> {'item' -> 'minecraft:string'},
                        'X' -> {'item' -> 'minecraft:dropper'},
                        'I' -> {'item' -> 'minecraft:stick'}
                    },
                    'result' ->  {
                        'item' ->  'minecraft:dispenser'
                    }
                },
                'dispenser2.json' -> {
                    'type' -> 'minecraft:crafting_shapeless',
                    'group' ->  'dispenser',
                    'ingredients' ->  [
                        {'item' -> 'minecraft:dropper'},
                        {'item' -> 'minecraft:bow'}
                    ],
                    'result' ->  {
                        'item' ->  'minecraft:dispenser'
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

