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
        go!: /AdressToSend

    state: FindDocuments
        a: Электронные документы вы можете скачать в личном кабинете в разделе "Мои документы"
        go!: /SomethingElse