// CONFIG:
global_event_interval = 40; // TICK


global_app_name = system_info('app_name');


__config()->{
    'stay_loaded' -> true,
    'commands' -> {
        'start' -> _() -> print('todo'),
        'stop' -> _() -> print('todo'),
        'status' -> _() -> print('todo'),
        'reloadcfg' -> _() -> print('todo'),
        'rules' -> _() -> print('todo'),
        'quickrefresh' -> 'quickrefresh',
        'simulate' -> _() -> print('todo'),
        'test' -> _() -> print('todo'),
        'test <streamer>' -> _(streamer) -> print('todo'),
        'execute <tsl_action>' -> 'execute'
    },
    'arguments' -> {
        'tsl_action' -> {
            'type' -> 'text',
            'suggest' -> [
                'DROP apple 5',
                'SHUFFLE armors DISPLAYING %["Pst, check your armors!"]%',
                'EITHER DROP stick OR CHANGE main-hand INTO stick OR NOTHING'
            ],
            // TODO: dynamic suggester
        },
        'streamer' -> {
            'type' -> 'term'
            // todo
        }
    }
};

// COMMANDS
quickrefresh() -> (
    global_queue = [];
    global_events = {};
    n = for(list_files('','text'),
        _parse(_)
    );
    print('tofix: Caricati '+ n +' file')
);
execute(tsl_action) -> (
    words = _intoWords(tsl_action);
    action = _parseAction(words:0, slice(words,1));
    
    player = player() || 'console';
    _processAction(action, _randomArgs(player))
);

// EVENT QUEUE
global_queue = [];
global_next_event = tick_time();

__on_tick() ->
if(global_next_event <= tick_time() && global_queue,
    global_next_event = tick_time() + global_event_interval;
    call(global_queue:0);
    delete(global_queue:0);
);

// TSL PARSER
global_events = {};
(
    global_re_MULTI_LINE_COMMENT_BEGIN = '#\\*';
    global_MULTI_LINE_COMMENT_BEGIN = '#*';
    global_re_MULTI_LINE_COMMENT_END = '\\*#';
    global_MULTI_LINE_COMMENT_END = '*#';
    global_re_COMMENT = '#';
    global_COMMENT = '#';
    global_re_SPACE = ' ';
    global_SPACE = ' ';
    global_re_GROUPING = '%';
    global_GROUPING = '%';
    global_re_ESCAPE = '\\\\';
    global_ESCAPE = '\\';

    global_DISPLAY_KEYWORD = 'DISPLAYING';
    global_EVENT_KEYWORD = 'ON';
    global_PREDICATE_KEYWORD = 'WITH';
);


_intoRules(script) -> (
    rule = '';
    rules = [];
    
    // Split newlines
    lines = split('\\R', replace(script, '(?s)' + global_re_MULTI_LINE_COMMENT_BEGIN + '.*?' + global_re_MULTI_LINE_COMMENT_END));
    
    // Check for unexpected beginning or ending of a multi-line comment
    for(lines,
        if(_ ~ global_re_MULTI_LINE_COMMENT_BEGIN != null,
            throw('TSLSyntaxError', 'exception', 'Unclosed multiline comment -> ' + _),
           _ ~ global_re_MULTI_LINE_COMMENT_END != null,
            throw('TSLSyntaxError', 'exception', 'Unexpected comment closing -> ' + _)
        );
    );

    // Traverse every line
    for(lines,
        // Skip comment lines
        if(_ ~ ('^[ \t]*' + global_re_COMMENT)!=null,
            continue();
        );

        // Trim valid comments
        escaping = false;
        inGroup = false;
        inJsonString = false;
        line = '';
        for(split('', _),
            // Comment character
            if(_ == global_COMMENT && !inGroup && !inJsonString, break());
            // Grouping character
            if(_ == global_GROUPING && !escaping, inGroup = !inGroup);
            // Json String
            if((_ == '"'  || _ == '\'') && !escaping, inJsonString = !inJsonString);
            // Escape character
            if(_ == global_ESCAPE, escaping = !escaping; line += _; continue());
            line += _;
            escaping = false
        );

        // Empty line (rule delimiter) occurred
        if(line ~ '^\\s*$' != null,
            if(length(rule) != 0, rules += rule);
            rule = '';
            continue();
        );

        // Starts with indent
        if(line ~ '^[ \t].*$' != null,
            if(length(rule) == 0,
                throw('TSLSyntaxError', 'exception', 'Invalid indent at line ' + (_i + 1))
            );
            rule += global_SPACE + (line ~ '^[ \t]*(.*?)[ \t]*$');
            continue();
        );

        // No indent & not empty line (rule delimiter)
        if(length(rule) != 0,
            throw('TSLSyntaxError', 'exception', 'Missing indent at line ' + (_i + 1))
        );
        rule = line ~ '^[ \t]*(.*?)[ \t]*$';
    );

    // Add last accumulation if not empty
    if(length(rule) != 0,
        rules += rule;
    );
    rules
);
_intoWords(rule) -> (
    words = [];
    word = '';

    inGroup = false;
    escaping = false;

    // Traverse each character and accumulate words
    for(split('',rule),
        // Escape character
        if(_ == global_ESCAPE,
            if(escaping, word += _);
            escaping = !escaping;
            continue()
        );

        // Grouping character
        if(_ == global_GROUPING,
            if(escaping, word += _; escaping = false; continue());
            inGroup = !inGroup;
            continue()
        );

        // Space character
        if(_ == global_SPACE,
            if(escaping, 
                throw('TSLSyntaxError', 'exception', 'Unexpected escaping on space character near -> ' + word)
            );
            if(!inGroup,
                if(length(word) != 0, words += word);
                word = '';
                continue()
            );
        );

        // None of them
        if(escaping,
            escaping = false;
            word += global_ESCAPE
        );
        word += _
    );

    // Add last accumulation if it is not empty
    if(length(word) != 0, words += word);
    
    // Still escaping
    if(escaping,
        throw('TSLSyntaxError', 'exception', 'Incomplete escape')
    );

    // Still in group
    if(inGroup,
        throw('TSLSyntaxError', 'exception', 'Incomplete grouping sequence')
    );

    words
);
_intoParts(words) -> (
    // Validate
    sdrow = map(sort_key(words, __ += 1),lower(_));
    indexDisplay = sdrow ~ lower(global_DISPLAY_KEYWORD);
    indexDisplay = if(indexDisplay != null, length(words) - indexDisplay);
    indexEvent = sdrow ~ lower(global_EVENT_KEYWORD);
    indexEvent = if(indexEvent != null, length(words) - indexEvent);
    indexPredicate = sdrow ~ lower(global_PREDICATE_KEYWORD);
    indexPredicate = if(indexPredicate != null, length(words) - indexPredicate);

    // No Event
    if(indexEvent == null,
        throw('TSLSyntaxError', 'exception', 'Expected at least one ' + global_EVENT_KEYWORD + ' statement.')
    );

    // Wrong Displayt
    if(indexDisplay && indexDisplay >= indexEvent,
        throw('TSLSyntaxError', 'exception', 'Found ' + global_DISPLAY_KEYWORD + ' statement on unexpected location.')
    );

    // Wrong Predicate
    if(indexPredicate && indexEvent >= indexPredicate,
        throw('TSLSyntaxError', 'exception', 'Found ' + global_EVENT_KEYWORD + ' statement on unexpected location.')
    );

    // Wrong Size
    if(length(words) < 3,
        throw('TSLSyntaxError', 'exception', 'Unexpected length of words -> ' + length(words))
    );

    // Tokenize Action
    actionName = words:0;
    actionParameters = [];
    wordCursor = 1;

    for(slice(words,wordCursor),
        if(lower(_) == lower(global_EVENT_KEYWORD),
            wordCursor += _i;
            break();
        );
        actionParameters += _;
    );

    // Tokenize Event
    if(lower(words:wordCursor) != lower(global_EVENT_KEYWORD),
        throw('TSLSyntaxError', 'exception', 'Expected ' + global_EVENT_KEYWORD + ' keyword, found word -> '+ words:wordCursor)
    );

    eventName = '';
    if((wordCursor += 1) < length(words),
        for([...slice(words, wordCursor), global_PREDICATE_KEYWORD],
            if(lower(_) == lower(global_PREDICATE_KEYWORD),
                wordCursor += _i;
                break(),
            // else
                eventName += _ + global_SPACE
            )
        )
    );

    eventName = eventName ~ '^[ \t]*(.*?)[ \t]*$';
    
    if(!eventName,
        throw('TSLSyntaxError', 'exception', 'Expected event name after ' + global_EVENT_KEYWORD + ' keyword')
    );

    predicateParameters = [];
    while(wordCursor < length(words), length(words),
        if(lower(words:wordCursor) != lower(global_PREDICATE_KEYWORD),
            throw('TSLSyntaxError', 'exception', 'Expected ' + global_PREDICATE_KEYWORD + ' keyword, found -> '+ words:wordCursor)
        );

        predicateWords = [];
        if((wordCursor += 1) < length(words),
            for([...slice(words, wordCursor), global_PREDICATE_KEYWORD],
                if(lower(_) == lower(global_PREDICATE_KEYWORD),
                    wordCursor += _i;
                    break(),
                // else
                    predicateWords += _
                )
            )
        );

        if(!predicateWords,
            throw('TSLSyntaxError', 'exception', 'Expected a word after ' + global_PREDICATE_KEYWORD + ' keyword')
        );
        predicateParameters += predicateWords
    );

    {
        'ActionName' -> actionName,
        'ActionParameters' -> actionParameters,
        'EventName' -> eventName,
        'PredicateParameters' -> predicateParameters
    }
);

global_actions = {
    'DROP' -> [true, {
        null -> _(parameters) -> (
            self = { 'message' -> _parseMessage(parameters) };
            actionWords = _actionPart(parameters);
            if(length(actionWords) != 1 && length(actionWords) != 2,
                throw('TSLSyntaxError', 'exception', 'Invalid length of words: ' + actionWords)
            );
            self:'itemRaw' = actionWords:0;
            self:'itemAmount' = if(length(actionWords) != 2, 1, number(actionWords:1));
            if(!self:'itemAmount',
                throw('TSLSyntaxError', 'exception', 'Expected an integer, found instead -> ' + actionWords:1)
            );
            // TODO: Check if given item word is parse-able
            self            
        ),
        'performAction' -> _(self, player, args) -> (
            input = _replaceExpressions(self:'itemRaw', args);
            itemStack = _parseItem(input);
            itemStack:1 = self:'itemAmount';
            nbt = nbt(str('{Item:{id:"%s",Count:%d}}',
                itemStack:0,
                itemStack:1
            ));
            nbt:'Item.tag' = itemStack:2;
            spawn('item', pos(player), nbt)
        ),
        'subtitleEvaluator' -> _(self, expression, args) -> (
            input = _replaceExpressions(self:'itemRaw', args);
            itemStack = _parseItem(input);

            if(
                expression == 'itemName', title(replace(itemStack:0, '_', ' ')),
                expression == 'itemCount', self:'itemAmount',
                null
            )
        )
    }],
    'SUMMON' -> [true, todo],
    'THROW' -> [true, todo],
    'CLEAR' -> [true, todo],
    'EXECUTE' -> [true, {
        null -> _(parameters) -> (
            self = { 'message' -> _parseMessage(parameters) };
            actionWords = _actionPart(parameters);
            if(!length(actionWords),
                throw('TSLSyntaxError', 'exception', 'Expected at least one command.')
            );

            if(!all(parameters, split('',_):0 == '/'),
                throw('TSLSyntaxError', 'exception', 'Every command must start with \'/\' character')
            );

            self:'commands' = parameters;
            self            
        ),
        'performAction' -> _(self, player, args) -> (
            for(self:'commands',
                command = join('',slice(split('',_replaceExpressions(_, args)),1));
                result = run(str('execute as %s run %s',player,command));
                // Wohooo we knew iGoodie liked hacky solutions. ( ? :/ )
                // If it yielded an error, and not worked as expected
                // Then turn on the feedback, and run it again! Brilliant! What could go wrong? :))))))
                if(result:0 <= 0,
                    run(command);
                );
                logger('info', str('[%s.sc] Executed (Status:%s) -> %s', global_app_name, result:0, command));
            )
        )
    }],
    'SHUFFLE' -> [true, todo],
    'CHANGE' -> [true, todo],
    'NOTHING' -> [true, todo],

    'EITHER' -> [false, todo],
    'BOTH' -> [false, todo],
    'FOR' -> [false, todo],
    'WAIT' -> [true, todo],
    'REFLECT' -> [true, todo],

    'OS_RUN' -> [true, todo],

    'PARKOUR_PENITENZE' -> [true,{
        null -> _(parameters) -> (
            self = { 'message' -> _parseMessage(parameters) };
            actionWords = _actionPart(parameters);
            if(length(actionWords) > 1,
                throw('TSLSyntaxError', 'exception', 'Expected at most one parameter')
            );
            self:'amount' = actionWords:0 || 0;
            self            
        ),
        'performAction' -> _(self, player, args) -> (
            amount = number(_replaceExpressions(self:'amount', args)) || 0;
            signal_event('player_takes_damage', player, player, amount, args:'ActorNickname', player(args:'ActorNickname'))
        )
    }

    ]
};
global_comparator = {
    'IN RANGE' -> _(left, right) -> (
        values = right~'^\\[(.+),(.+)\\]$';
        if(!values || type(values)!='list' || length(values)!=2,
            throw('TSLSyntaxError', 'exception', 'Expected format like [1.0,2.0], found -> ' + right)
        );
        [min,max] = map(values,number(_));
        if(min == null,
            throw('TSLSyntaxError', 'exception', 'Expected valid min number, found ->' + values:0)
        );
        if(max == null,
            throw('TSLSyntaxError', 'exception', 'Expected valid dmber, found ->' + values:1)
        );
        if(min > max,
            throw('TSLSyntaxError', 'exception', 'Expected first value to be less than the second value.')
        );
        number = number(left);
        number != null && min <= number && max >= number
    ),
    'CONTAINS' -> _(left, right) -> (
        left ~ right != null
    ),
    'IS' -> _(left, right) -> (
        lower(left) == lower(right)
    ), 
    'PREFIX' -> _(left, right) -> (
        type(left) == 'string' && left ~ ('^'+right) != null
    ), 
    'POSTFIX' -> _(left, right) -> (
        type(left) == 'string' && left ~ (right+'$') != null
    ), 
    '=' -> _(left, right) -> (
        value = number(right);
        if(value == null,
            throw('TSLSyntaxError', 'exception', 'Expected valid number, found ->' + value)
        );
        number = number(left);
        number != null && value == number
    ), 
    '>' -> _(left, right) -> (
        value = number(right);
        if(value == null,
            throw('TSLSyntaxError', 'exception', 'Expected valid number, found ->' + value)
        );
        number = number(left);
        number != null && number > value
    ), 
    '>=' -> _(left, right) -> (
        value = number(right);
        if(value == null,
            throw('TSLSyntaxError', 'exception', 'Expected valid number, found ->' + value)
        );
        number = number(left);
        number != null && number >= value
    ), 
    '<' -> _(left, right) -> (
        value = number(right);
        if(value == null,
            throw('TSLSyntaxError', 'exception', 'Expected valid number, found ->' + value)
        );
        number = number(left);
        number != null && number < value
    ), 
    '<=' -> _(left, right) -> (
        value = number(right);
        if(value == null,
            throw('TSLSyntaxError', 'exception', 'Expected valid number, found ->' + value)
        );
        number = number(left);
        number != null && number <= value
    ), 
};
global_predicate = {
    'ActorNickname' -> ['actor'],
    'Message' -> ['message'],
    'DonationCurrency' -> ['currency','donation_currency'],
    'DonationAmount' -> ['amount','donation_amount'],
    'SubscriptionMonths' -> ['months','subscription_months'],
    'SubscriptionTier' -> ['tier','subscription_tier'],
    'Gifted' -> ['gifted'],
    'ViewerCount' -> ['viewers','viewer_count'],
    'RaiderCount' -> ['raiders','raider_count'],
    'RewardTitle' -> ['title','reward_title'],
    'ChatBadges' -> ['badges','chat_badges']
};

_parseComparator(symbol) -> (
    comparator = global_comparator:upper(symbol);
    if(!comparator,
        throw('TSLSyntaxError', 'exception', 'Unknown comparator -> ' + symbol)
    );
    comparator
);
_parseAction(name, parameters) -> (
    actionClass = global_actions:name;
    if(!actionClass,
        throw('TSLSyntaxError', 'exception', 'Unknown action name -> ' + name)
    );
    {
        'Name' -> name,
        'Displaying' -> actionClass:0,
        'Action' -> actionClass:1,
        'Parameters' -> parameters
    }
);
_parsePredicates(parameters) -> (
    predicates = [];
    // For each word sequence
    for(parameters,
        if(length(_) < 3,
            throw('TSLSyntaxError', 'exception', 'Expected at least 3 words after ' + global_PREDICATE_KEYWORD + ', found instead -> ' + _)
        );
        remainingWords = _;

        // Consume rightmost and leftmost words, join remaining
        propertyName = remainingWords:0;
        right = remainingWords:(-1);
        symbol = join(' ', slice(remainingWords,1,-2));

        // Create predicate and accumulate
        predicates += {
            'PropertyName' -> propertyName, 
            'Comparator' -> _parseComparator(symbol),
            'Right' -> right
        }
    );
    predicates
);
_parseMessage(words) -> (
    messageKeywordCount = length(filter(words, lower(_) == lower(global_DISPLAY_KEYWORD)));
    if(!messageKeywordCount, return(null));
    if(messageKeywordCount != 1,
        throw('TSLSyntaxError', 'exception', 'Expected AT MOST one ' + global_DISPLAY_KEYWORD + ', found ' + messageKeywordCount + ' instead')
    );
    for(words,
        // Found the displaying keyword
        if(lower(_) == lower(global_DISPLAY_KEYWORD),
            // Range check
            if(_i + 1 >= length(words),
                throw('TSLSyntaxError', 'exception', 'Expected word after ' + global_DISPLAY_KEYWORD)
            );

            jsonString = words:(_i + 1);
            try(
                return(decode_json(jsonString));
            , 'json_error',
                throw('TSLSyntaxError', 'exception', 'Malformed JSON array -> ' + jsonString)
            )
        )
    );
    null
);
_parseItem(string) -> (
    item = string ~ '(.*?)(?:\\{.*|$)';
    nbt = string-item || '{}';
    [item, 1, nbt(nbt)]
);
_parse(name) -> (
    // tokenize into rules first
    script = join('\n', read_file(name, 'text'));
    if(!script,
        throw('TSLSyntaxError', 'exception', 'Empty file -> ' + name)
    );
    rules = _intoRules(script);

    syntaxErrors = [];
    // Traverse every rule
    for(rules,
        rule = _;
        try(
            words = _intoWords(rule);
            ruleParts = _intoParts(words);

            // Fetch event, or create one
            event = global_events:lower(ruleParts:'EventName') || {
                'EventName' -> ruleParts:'EventName',
                'Predicates' -> [],
                'Actions' -> []
            };

            // Parse action and predicates
            action = _parseAction(ruleParts:'ActionName',ruleParts:'ActionParameters');
            predicates = _parsePredicates(ruleParts:'PredicateParameters');

            // Chain all the nodes on event node
            event:'Predicates' += predicates;
            event:'Actions' += action;
            
            // Put event to where it belongs
            if(global_supported_events~lower(event:'EventName')!=null,
                global_events:lower(event:'EventName') = event,
            // else
                throw('TSLSyntaxError', 'exception', 'Unsupported event -> ' + event:'EventName')
            ),
        'TSLSyntaxError',
            syntaxErrors += [rule, _, _trace]
        )
    );
    if(syntaxErrors,
        throw('TSLSyntaxError', 'exception', syntaxErrors)
    );
    global_events
);

_actionPart(words, ... until) -> (
    until = if(!until, until = [global_DISPLAY_KEYWORD], until):0;
    actionPart = [];
    for(words,
        if(lower(_) == lower(until),
            break()
        );
        actionPart += _
    );
    actionPart
);
_replaceExpressions(input, args) -> (
    if(type(args) != 'function',
        _replaceExpressions(input, _(expression, outer(args)) -> (
            evaluation = if(
                expression == 'event', args:'EventName',
                expression == 'message', escape_nbt(args:'Message'),
                expression == 'message_unescaped', args:'Message',
                expression == 'title', args:'RewardTitle',
                expression == 'actor', args:'ActorNickname',
                expression == 'streamer', args:'StreamerNickname',
                expression == 'amount' && args:'DonationAmount', args:'DonationAmount',
                expression == 'amount_i' && args:'DonationAmount', floor(args:'DonationAmount'),
                expression == 'amount_f' && args:'DonationAmount', str('%.2f',args:'DonationAmount'),
                expression == 'currency' && args:'DonationCurrency', args:'DonationCurrency',
                expression == 'months' && args:'SubscriptionMonths', args:'SubscriptionMonths',
                expression == 'tier' && args:'SubscriptionTier' != -1, 
                    if(args:'SubscriptionTier' == 0,
                        'Prime',
                        args:'SubscriptionTier'
                    ),
                expression == 'gifted', args:'Gifted',
                expression == 'viewers' && args:'ViewerCount', args:'ViewerCount',
                expression == 'raiders' && args:'RaiderCount', args:'RaiderCount',
                expression == 'date', str('%3$02d-%2$02d-%1$04d', convert_date(unix_time())),
                expression == 'date_utc', str('%3$02d-%2$02d-%1$04d', convert_date(unix_time())), // TODO FIX
                expression == 'time',  str('%02d:%02d:%02d', slice(convert_date(unix_time()),3)),
                expression == 'time_utc', str('%02d:%02d:%02d', slice(convert_date(unix_time()),3)), // TODO FIX
                expression == 'unix', unix_time(),
                null                
            );
            if(evaluation, evaluation, '${' + expression + '}')
        )),
    // else
        string = '';       
        while(input, length(input),
            expression = input ~ '\\$\\{(.*?)\\}';
            if(expression != null,
                prefix = split('\\$\\{.*?\\}', input):0 || '';
                string += prefix;
                string += call(args, expression);
                index = length(prefix) + length(expression) + 3;
                input = if(index < length(input), slice(input, index), ''),
            // else
                string += input;
                input = '';
            );
        );
        string
    )
);

_extractArg(args, name) -> (
    for(global_predicate,
        if(global_predicate:_ ~ lower(name) != null,
            return(_); 
        )
    );
    throw('TSLRuntimeError', 'exception', 'Unknown tsl predicate field -> '+name)
);
_notifyPlayer(player, action, args) -> (todo);
_performAction(player, action, args) -> (
    self = call(action:'Action':null, action:'Parameters');
    call(action:'Action':'performAction', self, player, args)
);
_processPredicate(predicate, args) -> (
    logger('debug', str('[%s.sc] Reached TSLPredicate node -> %s with %s', global_app_name, predicate, args));
    value = _extractArg(args, predicate:'PropertyName');
    if(args:value == null,
        throw('TSLRuntimeError', 'exception', 'Unknown tsl predicate field -> '+value)
    );
    call(predicate:'Comparator', args:value, predicate:'Right')
);
_processAction(action, args) -> (
    player = action:'ReflectedUser' || player(args:'StreamerNickname') || player();
    if(!player,
        logger('info', str('[%s.sc] Player %s is not found. Skipping %s event.', global_app_name, args:'StreamerNickname', action:'Name'));
        return(false)
    );
    if(!action:'Silent',
        _notifyPlayer(player, action, args)
    );
    _performAction(player, action, args);
    logger('info', str('[%s.sc] %s action performed for %s', global_app_name, action:'Name', player));
    true
);
_processEvent(event_name, args) -> (
    if(event=global_events:lower(event_name),
        if(length(event:'Predicates') != length(event:'Actions'),
            throw('TSLRuntimeError', 'exception', event_name + ' has uneven Predicates and Actions')
        );
        for(event:'Predicates',
            if(reduce(_, _a && _processPredicate(_, args), true),
                if(_processAction(event:'Actions':_i, args),
                    return(true)
                )
            )
        )
    );
    return(false)
);

_randomArgs(player, ... info) -> (
    info = if(info && info:0, info:0, {});
    args = {
        'EventName' -> info:'EventName' || title(rand(global_supported_events)),
        'StreamerNickname' -> info:'StreamerNickname' || player() || 'RandomStreamer',
        'ActorNickname' -> info:'ActorNickname' || 'RandomDude',
        'Message' -> info:'Message' || 'Random event message',
        'DonationAmount' -> info:'DonationAmount' || rand(1000),
        'DonationCurrency' -> info:'DonationCurrency' || rand(['USD', 'TRY', 'EUR']),
        'SubscriptionMonths' -> info:'SubscriptionMonths' || floor(rand(99)+1),
        'SubscriptionTier' -> info:'SubscriptionTier' || floor(rand(4)),
        'Gifted' -> info:'Gifted' || bool(rand(2)),
        'ViewerCount' -> info:'ViewerCount' || floor(rand(99)+1),
        'RaiderCount' -> info:'RaiderCount' || floor(rand(99)+1)
    }
);

// TWITCH EVENTS
global_supported_events = [
    'twitch follow',
    'twitch subscription gift',
    'twitch subscription',
    'twitch host',
    'twitch raid',
    'donation',
    'twitch chat message',
    'twitch channel point reward',
    'twitch bits'
];
__on_twitch_follow(player, actor) -> (
    args = {
        'EventName' -> 'Twitch Follow',
        'ActorNickname' -> actor,
        'StreamerNickname' -> player
    };
    global_queue += (_(outer(args)) -> _processEvent(args:'EventName', args));
    logger('debug', str('[%s.sc] Queued %s event', global_app_name, args:'EventName'))
);
__on_twitch_subscription_gift(player, actor, tier, amount) -> (
    args = {
        'EventName' -> 'Twitch Subscription Gift',
        'ActorNickname' -> actor,
        'StreamerNickname' -> player,
        'DonationAmount' -> amount,
        'SubscriptionTier' -> tier,
        'Gifted' -> true
    };
    global_queue += (_(outer(args)) -> _processEvent(args:'EventName', args));
    logger('debug', str('[%s.sc] Queued %s event', global_app_name, args:'EventName'))
);
__on_twitch_subscription(player, actor, message, tier, months, resubbed, streak, gifted, gifter) -> (
    args = {
        'EventName' -> 'Twitch Subscription',
        'Message' -> message,
        'ActorNickname' -> actor,
        'StreamerNickname' -> player,
        'SubscriptionMonths' -> months,
        'SubscriptionTier' -> tier,
        'Gifted' -> gifted
    };
    global_queue += (_(outer(args)) -> _processEvent(args:'EventName', args));
    logger('debug', str('[%s.sc] Queued %s event', global_app_name, args:'EventName'))
);
__on_twitch_host(player, actor, viewers) -> (
    args = {
        'EventName' -> 'Twitch Host',
        'ActorNickname' -> actor,
        'StreamerNickname' -> player,
        'ViewerCount' -> viewers
    };
    global_queue += (_(outer(args)) -> _processEvent(args:'EventName', args));
    logger('debug', str('[%s.sc] Queued %s event', global_app_name, args:'EventName'))
);
__on_twitch_raid(player, actor, viewers) -> (
    args = {
        'EventName' -> 'Twitch Raid',
        'ActorNickname' -> actor,
        'StreamerNickname' -> player,
        'RaiderCount' -> viewers
    };
    global_queue += (_(outer(args)) -> _processEvent(args:'EventName', args));
    logger('debug', str('[%s.sc] Queued %s event', global_app_name, args:'EventName'))
);
__on_streamlabs_donation(player, actor, message, amount, formattedAmount, currency) -> (
    args = {
        'EventName' -> 'Donation',
        'Message' -> message,
        'ActorNickname' -> actor,
        'StreamerNickname' -> player,
        'DonationAmount' -> amount,
        'DonationCurrency' -> currency
    };
    global_queue += (_(outer(args)) -> _processEvent(args:'EventName', args));
    logger('debug', str('[%s.sc] Queued %s event', global_app_name, args:'EventName'))
);
__on_twitch_bits(player, actor, message, amount) -> (
    args = {
        'EventName' -> 'Twitch Bits',
        'Message' -> message,
        'ActorNickname' -> actor,
        'StreamerNickname' -> player,
        'DonationAmount' -> amount
    };
    global_queue += (_(outer(args)) -> _processEvent(args:'EventName', args));
    logger('debug', str('[%s.sc] Queued %s event', global_app_name, args:'EventName'))
);
__on_twitch_chat_message(player, actor, message, badges, subscriptionMonths) -> (
    args = {
        'EventName' -> 'Twitch Chat Message',
        'Message' -> message,
        'ActorNickname' -> actor,
        'StreamerNickname' -> player,
        'SubscriptionMonths' -> subscriptionMonths,
        'ChatBadges' -> badges
    };
    global_queue += (_(outer(args)) -> _processEvent(args:'EventName', args));
    logger('debug', str('[%s.sc] Queued %s event', global_app_name, args:'EventName'))
);
__on_twitch_custom_reward(player, actor, message, badges, subscriptionMonths, customRewardId) -> (
    args = {
        'EventName' -> 'Twitch Channel Point Reward',
        'Message' -> message,
        'ActorNickname' -> actor,
        'StreamerNickname' -> player,
        'SubscriptionMonths' -> subscriptionMonths,
        'ChatBadges' -> badges,
        'RewardTitle' -> customRewardId
    };
    global_queue += (_(outer(args)) -> _processEvent(args:'EventName', args));
    logger('debug', str('[%s.sc] Queued %s event', global_app_name, args:'EventName'))
);
// INITIALIZE
quickrefresh();
