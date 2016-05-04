<?php
	if($_GET['classname']=='A')
	{
		echo json_encode(array("A","B","C","张三"));
	}
	else
	{
		echo json_encode(array("D","E","F","李四"));
	}