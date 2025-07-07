theme: /
    state: Partnership
        a: Мы рады, что вы хотели бы с нами посотрудничать! Пожалуйста, опишите ваше предложение в одном сообщении!
        go!: /PartnershipReqested
        
        state: PartnershipReqested
            event: noMatch
            script:
                $session.partnershipReqested = $request.query;
            a: Отлично, я передал ваше предложение коллегам!
               Они свяжутся с вами в течение недели, если предложение нас заинтересует!
            go!: /SomethingElse