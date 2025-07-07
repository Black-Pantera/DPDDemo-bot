require: slotfilling/slotFilling.sc
  module = sys.zb-common
require: dateTime/moment.min.js
    module = sys.zb-common   
require: common.js
    module = sys.zb-common

init:
    var SESSION_TIMEOUT_MS = 86400000; // Один день
    
    bind("onAnyError", function($context) {
        var answers = [
            "Извините, произошла техническая ошибка. Специалисты обязательно изучат её и возьмут в работу. Пожалуйста, напишите в чат позже.",
            "Простите, произошла ошибка в системе. Наши специалисты обязательно её исправят."
        ];
        var randomAnswer = answers[$reactions.random(answers.length)];
        $reactions.answer(randomAnswer);
           
        $reactions.buttons({ text: "В главное меню", transition: "/Start" })
    }); 
    
    bind("preProcess", function($context) {
        if (!$context.session.stateCounter) {
            $context.session.stateCounter = 0;
        }
        
        if (!$context.session.stateCounterInARow) {
            $context.session.stateCounterInARow = 0;
        }
        
        if ($context.session.lastActiveTime) {
            var interval = $jsapi.currentTime() - $context.session.lastActiveTime;
            if (interval > SESSION_TIMEOUT_MS) $jsapi.startSession();
        }
    });
        
    bind("postProcess", function($context) {
        $context.session.lastState = $context.currentState;
        $context.session.lastActiveTime = $jsapi.currentTime();
        
        $context.session.stateCounter = 0;
        $context.session.stateCounterInARow = 0;
        
    });

theme: /
    
    state: Start
        q!: $regex</start>
        a: Добрый день!
            Я бот-помощник компании DPD. Помогу вам отследить или изменить заказ, назначить дату и время доставки, найти нужные документы.
            Буду рад помочь вам! || htmlEnabled = true, html = "Добрый день!<br>Я бот-помощник компании DPD. Помогу вам отследить или изменить заказ, назначить дату и время доставки, найти нужные документы.<br>Буду рад помочь вам!"
        go!: /HowCanIHelp
        
    state: GlobalCatchAll || noContext = true
        event!: noMatch
        script:
            $session.stateCounterInARow++
                
        if: $session.stateCounterInARow < 3
            random: 
                a: Прошу прощения, не совсем вас понял. Попробуйте, пожалуйста, переформулировать ваш вопрос.
                a: Простите, не совсем понял. Что именно вас интересует?
                a: Простите, не получилось вас понять. Переформулируйте, пожалуйста.
                a: Не совсем понял вас. Пожалуйста, попробуйте задать вопрос по-другому.
        else:
            a: Кажется, этот вопрос не в моей компетенции. Но я постоянно учусь новому, и, надеюсь скоро научусь отвечать и на него.
                
            script: 
                $session.stateCounterInARow = 0
                    
            go!: /SomethingElse

    state: Hello
        intent!: /привет
        a: Добрый день!
            Рад, что вы снова на связи!
        go!: /HowCanIHelp

    state: Bye
        intent!: /пока
        a: Надеюсь, я был вам полезен! До встречи!

    state: NoMatch
        event!: noMatch
        a: Простите, я не понял ваш вопрос. Перевожу диалог на оператора

    state: Match
        event!: match
        a: {{$context.intent.answer}}

    state: HowCanIHelp
        random: 
            a: Подскажите, какой у вас вопрос?
            a: Чем я могу быть полезен вам сегодня?
            a: Что я могу сделать для вас сегодня?
        buttons:
            "Где мой заказ?" -> /TreckOrder
            "Нужны документы" -> /ChooseDocuments
            "Изменить заказ" -> /ChangeOrder
            "Сотрудничество" -> /Partnership
            "Другой вопрос" -> /OtherQuestions
        event: noMatch || toState = "./"

    state: TreckOrder
        random: 
            a: Укажите, пожалуйста, номер вашего заказа
            a: Подскажите номер вашего заказа
            a: Скажите, пожалуйста, какой номер у вашего заказа?
        intent: /123 || toState = "/TreckOrder/LateOrder"
        intent: /234 || toState = "/TreckOrder/OrderArrived"
        intent: /345 || toState = "/TreckOrder/OrderInProgress"
        
        state: LateOrder
            a: Вижу, что ваш заказ задерживается из-за проблем на сортировочном пункте.
                Нам очень жаль, что первоначальная дата доставки сдвинулась, но мы постараемся привезти ваш заказ 25.07.2025 с 10:00 до 18:00. 
                Благодарим вас за ожидание и понимание! || htmlEnabled = true, html = "Вижу, что ваш заказ задерживается из-за проблем на сортировочном пункте.<br><br>Нам очень жаль, что первоначальная дата доставки сдвинулась, но мы постараемся привезти ваш заказ 25.07.2025 с 10:00 до 18:00.&nbsp;<br><br>Благодарим вас за ожидание и понимание!"
            go!: /SomethingElse
            
        state: OrderArrived
            a: Текущий статус вашего заказа "Прибыл в пункт выдачи".
                Вы можете забрать его по адресу: г Санкт-Петербург, пр-кт Лиговский, дом 101, стр. 
                Время работы пункта: 10:00-22:00.
                Будем ждать вас"
            go!: /SomethingElse
            
        state: OrderInProgress
            a: Текущий статус вашего заказа "В пути".
                Ориентировочная дата прибытия заказа 19.07.2025  в пункт выдачи по адресу: г Санкт-Петербург, ул Белы Куна, дом 16, корп. 4, стр. Б.
                Когда заказ поступит в пункт выдачи, вы получите уведомление!
            go!: /SomethingElse
            
        state: LocalCatchAll || noContext = true
            event: noMatch
            script:
                $session.stateCounterInARow ++;
            if: $session.stateCounterInARow < 3
                script:
                    var answers = ["Извините, не совсем понял вас. Какой номер заказа?",
                            "К сожалению, не понял вас. Укажите валидный номер заказа."];
                    var randomAnswer = answers[$reactions.random(answers.length)];
                        $reactions.answer(randomAnswer);
            else: 
                go!:  /SomethingElse

    state: Documents
        event: noMatch || toState = "./"
        a: Вам нужны оригиналы или электронная версия документов?
        buttons:
            "Оригиналы" -> /SendDocuments
            "Электронные" -> /FindDocuments

    state: ChangeOrder
        random: 
            a: Укажите, пожалуйста, номер вашего заказа
            a: Подскажите номер вашего заказа
            a: Скажите, пожалуйста, какой номер у вашего заказа?
        go!: /WhatToChange

    state: Partnership
        a: Мы рады, что вы хотели бы с нами посотрудничать! Пожалуйста, опишите ваше предложение в одном сообщении!
        go!: /PartnershipReqested

    state: OtherQuestions
        TransferToOperator: 
            titleOfCloseButton = 
            messageBeforeTransfer = Уже подключаю оператора для ответа на ваш вопрос
            messageBeforeTransferHtml = 
            prechatAttributes = {}
            ignoreOffline = false
            messageForWaitingOperator = 
            messageForWaitingOperatorHtml = 
            sendMessageHistoryAmount = 
            sendMessagesToOperator = false
            actions = 
            htmlEnabled = false
            destination = 
            noOperatorsOnlineState = 
            dialogCompletedState = 

    state: ChooseDocuments
        a: Подскажите, какие именно документы вам нужны?
        buttons:
            "Накладная"
            "Акт сверки" -> /Documents
            "Счет-фактура" -> /Documents
            "Закрывающие" -> /Documents
            "Все документы" -> /Documents

    state: SendDocuments
        a: По какому адресу выслать указанные документы?
        go!: /AdressToSend

    state: FindDocuments
        a: Электронные документы вы можете скачать в личном кабинете в разделе "Мои документы"
        go!: /SomethingElse

    state: AdressToSend
        a: Отлично, ожидайте заказное письмо с оригиналами документов в течение семи рабочих дней!
        go!: /SomethingElse

    state: SomethingElse
        random: 
            a: Могу ли я помочь вам чем-то еще?
            a: Чем еще я могу вам помочь?
        buttons:
            "Где мой заказ?" -> /TreckOrder
            "Нужны документы" -> /ChooseDocuments
            "Изменить заказ" -> /ChangeOrder
            "Сотрудничество" -> /Partnership
            "Другой вопрос" -> /OtherQuestions
            "Я все узнал" -> /Bye

    state: PartnershipReqested
        a: Отлично, я передал ваше предложение коллегам!
                Они свяжутся с вами в течение недели, если предложение нас заинтересует!
        go!: /SomethingElse

    state: WhatToChange
        a: Вот что мне удалось найти по вашему заказу №123:
            
            Доставка запланирована на 01.08.2025 в пункт выдачи по адресу: г Санкт-Петербург, ул Захарьевская, дом 25, корп. А, пав. БЦ ’’Z-25’.
            
            
            Скажите, пожалуйста, какие данные необходимо изменить? || htmlEnabled = true, html = "Вот что мне удалось найти по вашему заказу №123:<br><br>Доставка запланирована на 01.08.2025 в пункт выдачи по адресу: г Санкт-Петербург, ул Захарьевская, дом 25, корп. А, пав. БЦ ’’Z-25’. <br><br><br>Скажите, пожалуйста, какие данные необходимо изменить?"
        buttons:
            "Время доставки"
            "Дату доставки"
            "Адрес доставки"
            "Получателя" -> /NewReciever

    state: NewReciever
        a: Пожалуйста, введите ФИО нового получателя
        go!: /ChangesDone

    state: ChangesDone
        a: Отлично, теперь получателем по заказу №123 является Иванов Иван Иванович
        go!: /SomethingElse