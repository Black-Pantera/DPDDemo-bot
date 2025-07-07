theme: /
    state: ChooseDocuments
        a: Подскажите, какие именно документы вам нужны?
        buttons:
            "Накладная" -> /ChooseDocuments/Documents
            "Акт сверки" -> /ChooseDocuments/Documents
            "Счет-фактура" -> /ChooseDocuments/Documents
            "Закрывающие" -> /ChooseDocuments/Documents
            "Все документы" -> /ChooseDocuments/Documents
            
        state: Documents
            event: noMatch || toState = "./"
            a: Вам нужны оригиналы или электронная версия документов?
            buttons:
                "Оригиналы" -> /SendDocuments
                "Электронные" -> /FindDocuments

    state: SendDocuments
        a: По какому адресу выслать указанные документы?
        
        state: AddressToSend
            event: noMatch
            script:
                $session.addressToSendDocuments = $request.query;
            a: Отлично, ожидайте заказное письмо с оригиналами документов по адресу {{$session.addressToSendDocuments}} в течение семи рабочих дней!
            go!: /SomethingElse

    state: FindDocuments
        a: Электронные документы вы можете скачать в личном кабинете в разделе "Мои документы"
        go!: /SomethingElse