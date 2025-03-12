<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Sending Email with Freemarker HTML Template Example</title>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

    <link href='http://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'>

    <!-- use the font -->
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            font-size: 48px;
        }
		table,tr,td,th {
			text-align: center;
			font-family: 'Times New Roman', Times, serif;
			font-size: medium;
			border-collapse: collapse;
			adding: 6px;
			width:90% ;
			table-layout: auto;
			background-color: #eaeaea;
			padding: 10px 5px 10px 5px;
		}
    </style>
</head>
<body style="margin: 0; padding: 0;">

    <table align="center" border="0" cellpadding="0" cellspacing="0" width="600" style="border-collapse: collapse;">
        <tr>
            <td align="center" bgcolor="#eaeaea" style="padding: 40px 0 30px 0; width: 300px;" >
                <img src="${imagePath}" style="display: block; alt="BC Hydro" style="width: 200px;height: 65px;" />
            </td>
        </tr>
        <tr>
            <td >
                <p>Dear Customer,</p>
				<p>${curDate}</p>
            </td>
        </tr>
		<tr>	<th colspan='2'>Description of alert</th>			</tr>
		<tr>	<td>${description}</td>		</tr>
		
		<tr>	<td><br><br>Thank you</td>		</tr>
    </table>
		

</body>
</html>