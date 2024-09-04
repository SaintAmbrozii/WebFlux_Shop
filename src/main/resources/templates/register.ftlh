<#ftl encoding="UTF-8">
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Вы выкупили билеты</title>
    <style>
        #body {
            display: flex;
            flex-direction: column;
            font-family: "Segoe UI", serif;
            align-items: center;
            background-color: #607FF2;
            font-size: 20px;
            height: 100%;
        }

        #div {
            display: block;
            flex-direction: column;
            margin: 10%;
            font-family: "Segoe UI", serif;
            background-color: white;
            padding: 3%;
            border-radius: 20px;
            color: black;
        }

        #button {
            background-color: #607FF2;
            border-radius: 10px;
            padding: 5px;
            color: white;
            text-decoration: none;
        }

        #a {
            text-decoration: none;
        }
    </style>
</head>
<body>
<div id="body">
    <div id="div">
        <h1 style="text-align: center">Регистрация!</h1>
        <p>Здорова, ${name}!</p>
        <p>ТЫ зарегистрировался в интернет магазине!</p>
        <div>
            <hr>
            <p>Код регистрации подтвердить! ${number}</p>

            <hr>
        </div>
        <p>Ну че уставился то? Не видел никогда чтоли?</p>
        <p>Этот год был трудным, впереди нас ждут честные выборы! <br> Владимир Владимирович Путин</p>

    </div>
</div>
</body>
</html>