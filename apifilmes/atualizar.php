<?php
    include_once("database.php");

    $conexao = getDatabaseConnection();
	if (!empty($_POST["imagem"])) {
		$imagem = base64_decode($_POST["imagem"]);
		$arquivo = md5(uniqid() . time()) . ".png";
		file_put_contents("imagens/" . $arquivo, $imagem);
		$statement = $conexao->prepare("UPDATE tbl_filme SET sinopse = ?, link = ?, titulo = ?, avaliacao = ?, imagem = ? WHERE id = ?");
		$statement->bind_param("sssdsi", ...array($_POST["sinopse"], $_POST["link"], $_POST["titulo"], $_POST["avaliacao"], $arquivo, $_POST["id"]));
	} else {
		$statement = $conexao->prepare("UPDATE tbl_filme SET sinopse = ?, link = ?, titulo = ?, avaliacao = ? WHERE id = ?");
		$statement->bind_param("sssdi", ...array($_POST["sinopse"], $_POST["link"], $_POST["titulo"], $_POST["avaliacao"], $_POST["id"]));
	}

    if ($statement->execute()) {
        $sucesso = true;
    } else {
        $sucesso = false;
    }

    $statement->close();
    $conexao->close();
    echo(json_encode(array("sucesso" => $sucesso)));
?>