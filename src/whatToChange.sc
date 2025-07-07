theme: /
    state: WhatToChange
        a: Вот что мне удалось найти по вашему заказу №123:
           Доставка запланирована на 01.08.2025 в пункт выдачи по адресу: г Санкт-Петербург, ул Захарьевская, дом 25, корп. А, пав. БЦ ’’Z-25’.
           Скажите, пожалуйста, какие данные необходимо изменить? || htmlEnabled = true, html = "Вот что мне удалось найти по вашему заказу №123:<br><br>Доставка запланирована на 01.08.2025 в пункт выдачи по адресу: г Санкт-Петербург, ул Захарьевская, дом 25, корп. А, пав. БЦ ’’Z-25’. <br><br><br>Скажите, пожалуйста, какие данные необходимо изменить?"
        buttons:
            "Время доставки" -> /WhatToChange/DeliveryTime
            "Дату доставки" -> /WhatToChange/DeliveryDate
            "Адрес доставки" -> /WhatToChange/DeliveryAddress
            "Получателя" -> /WhatToChange/NewReciever
            
        state: DeliveryTime
            a: Укажите желаемое время доставки.
            
            state: Time
                q: * @duckling.time *
                script:
                    $session.deliveryTime = $parseTree["_duckling.time"];
                    $temp.time = $session.deliveryTime;
                a: Время доставки перенесено на {{$temp.time.hour}}:{{$temp.time.minute}}.
                go!: /SomethingElse
                    
            state: LocalCatchAll
                event: noMatch
                script:
                    $session.stateCounterInARow ++
                if: $session.stateCounterInARow < 3
                    script:
                        if ($parseTree["_duckling.time"]) {
                            $reactions.answer("К сожалению, не могу принять такой ответ. Пожалуйста, введите корректное время.");
                            }
                        else {
                            var answers = ["Извините, не совсем понял вас. Укажите время доставки.",
                            "К сожалению, не понял вас. На какое время перенести доставку."];
                            var randomAnswer = answers[$reactions.random(answers.length)];
                            $reactions.answer(randomAnswer);
                            }
                else:
                    script: 
                        $reactions.transition("/SomethingElse");
                    
        state: DeliveryDate
            a: Укажите желаемую дату доставки.
            
            state: Date
                q: * @duckling.date *
                script:
                    if ($parseTree["_duckling.date"]) {
                        $session.deliveryDate = getUserDate($parseTree["_duckling.date"]); 
                        $temp.userFormatDate = moment($session.deliveryDate).format('LL');
                        $reactions.answer("Изменили дату доставки на "+$temp.userFormatDate);
                    }
                go!: /SomethingElse
                    
            state: LocalCatchAll
                event: noMatch
                script:
                    $session.stateCounterInARow ++
                if: $session.stateCounterInARow < 3
                    script:
                        if ($parseTree["_duckling.date"]) {
                            $reactions.answer("К сожалению, не могу принять такой ответ. Пожалуйста, введите актуальную дату.");
                            }
                        else {
                            var answers = ["Извините, не совсем понял вас. Какого числа ожидаете доставку?",
                            "К сожалению, не понял вас. На какую дату планируете доставку?"];
                            var randomAnswer = answers[$reactions.random(answers.length)];
                            $reactions.answer(randomAnswer);
                            }
                else:
                    script: 
                        $reactions.transition("/SomethingElse");
                       
        state: DeliveryAddress
            a: Укажите адресс доставки.
            
            state: Comment
                script:
                    $session.deliveryAddress = $request.query;
                    $reactions.answer("Изменили адрес доставки на " + $session.deliveryAddress);
                go!: /SomethingElse
                
        state: LocalCatchAll
            event: noMatch
            script: 
                $reactions.transition("/SomethingElse");
                
