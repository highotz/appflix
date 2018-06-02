<?php
    include_once("database.php");
    $conexao = getDatabaseConnection();

    $imagem = base64_decode($_POST["imagem"]);
    $arquivo = md5(uniqid() . time()) . ".png";
    file_put_contents("imagens/" . $arquivo, $imagem);

    $statement = $conexao->prepare("INSERT INTO tbl_filme (sinopse, link, titulo, avaliacao, imagem) VALUES (?, ?, ?, ?, ?)");
    $statement->bind_param("sssds", ...array($_POST["sinopse"], $_POST["link"], $_POST["titulo"], $_POST["avaliacao"], $arquivo));
    if ($statement->execute()) {
        $sucesso = true;
    } else {
        $sucesso = false;
    }

    $statement->close();
    $conexao->close();
    echo(json_encode(array("sucesso" => $sucesso)));
?>