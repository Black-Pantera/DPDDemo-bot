theme: /
    state: TreckOrder
        random: 
            a: Укажите, пожалуйста, номер вашего заказа
            a: Подскажите номер вашего заказа
            a: Скажите, пожалуйста, какой номер у вашего заказа?
        intent: /123 || toState = "/TreckOrder/LateOrder"
        intent: /234 || toState = "/TreckOrder/OrderArrived"
        intent: /345 || toState = "/TreckOrder/OrderInProgress"
        
        state: LateOrder
            a: Вижу, что ваш заказ №123 задерживается из-за проблем на сортировочном пункте.
                Нам очень жаль, что первоначальная дата доставки сдвинулась, но мы постараемся привезти ваш заказ 25.07.2025 с 10:00 до 18:00.
                Благодарим вас за ожидание и понимание! 
            go!: /SomethingElse
            
        state: OrderArrived
            a: Текущий статус вашего заказа №234 "Прибыл в пункт выдачи".
                Вы можете забрать его по адресу: г Санкт-Петербург, пр-кт Лиговский, дом 101, стр. 
                Время работы пункта: 10:00-22:00.
                Будем ждать вас"
            go!: /SomethingElse
            
        state: OrderInProgress
            a: Текущий статус вашего заказа №345 "В пути".
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