<?php
	//echo $_POST['aaa'];
	$raw_post_data = file_get_contents('php://input', 'r'); 
	echo $raw_post_data;
	//php://input
	//echo(json_encode($_POST));