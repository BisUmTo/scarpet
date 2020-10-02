__config() -> {'stay_loaded' -> true};

__on_twitch_chat_message(playerNick, actor, message, badges, subscriptionMonths) -> (
    if(_has_permission(badges,actor),print_message(actor, message, badges, subscriptionMonths))
);

global_permissions = ['broadcaster','moderator','vip','subscriber','sub-gifter','partner'];

_has_permission(badges,actor) -> (
    return(actor!='NomeBot1' && actor!='NomeBot2')
);

print_message(actor, message, badges, subscriptionMonths) -> (
    color = '';
    role = '';
    if( badges~'subscriber' != null, color='#06A396');
    if( badges~'vip' != null, color='#FF00FF');
    if( badges~'moderator' != null, color='#00FF00'; role='[Mod]');
    if( badges~'broadcaster', color='#FFD700'; role='[Streamer]');
    if( actor=='BisUmTo', color='#FF0000'; role='[ModCreator]');
    run(str('tellraw @a [{"text":"<"},{"text":"%s %s","color":"%s"},{"text":"> ","color":"white"},{"text":"%s"}]', role, actor, color, message))
)
