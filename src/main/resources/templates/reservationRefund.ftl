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
         width:30% ;
         table-layout: auto;
         background-color: #eaeaea;
         padding: 10px 5px 10px 5px;
         }
      </style>
   </head>
   <body style="margin: 0; padding: 0;">
      <table align="center" border="0" cellpadding="0" cellspacing="0" width="600" style="border-collapse: collapse; width: 600px;">
         <tr>
            <td align="center" bgcolor="#eaeaea" style="padding: 40px 200px 30px 200px; width: 300px;" >
               <img src="${imagePath}" style="display: block; width: 200px;height: 65px;" alt="BC Hydro"/>
            </td>
         </tr>
         <td style="padding: 0in 10pt;" xml="lang" class="">
            <p class="" style="text-align: left; font-size: 11pt; font-family: Calibri, sans-serif; margin-bottom: 10pt; line-height: 18.75pt;"><span style="font-size: 15pt; font-family: Arial, sans-serif; color: black;" class="">Dear </span><span style="font-size: 15pt; font-family: Arial, sans-serif;" class="">${userName}<span style="color: black;" class="">, </span></span></p>
            <p class="" style="text-align: left; font-size: 11pt; font-family: Calibri, sans-serif; margin-bottom: 7.5pt; line-height: 18.75pt;"><span style="font-size: 15pt; font-family: Arial, sans-serif; color: black;" class="">Your reservation was cancelled due to some Technical Issue. </span></p><p class="" style="text-align: left; font-size: 11pt; font-family: Calibri, sans-serif; margin-bottom: 7.5pt; line-height: 18.75pt;"><span style="font-size: 15pt; font-family: Arial, sans-serif; color: black;" class="">Reservation amout was added to Your ${whiteLabelName} Wallet. </span></p>
            <p class="" style="text-align: left; font-size: 11pt; font-family: Calibri, sans-serif; margin-bottom: 10pt; line-height: 22.5pt;"><span style="font-size: 15pt; font-family: Arial, sans-serif; color: black;" class="">Questions? Contact us 24 hours a day at <a href="tel:${contactUsNo}" target="_blank" class="" style=""><strong class="" style=""><span style="text-decoration: none; color: rgb(1, 19, 82);" class="">${contactUsNo}</span></strong></a>. </span></p>
			<p class="" style="text-align: left; font-size: 11pt; font-family: Calibri, sans-serif; margin-bottom: 7.5pt; line-height: 18.75pt;"><span style="font-size: 15pt; font-family: Arial, sans-serif; color: black;" class="">${curDate} </span></p>
            <p class="" style="text-align: left; font-size: 11pt; font-family: Calibri, sans-serif; margin-bottom: 10pt;  line-height: 18.75pt;" align="center"><strong class="" style=""><span style="font-size: 15pt; font-family: Arial, sans-serif; color: black;" class="">Reservation Details. </span></strong></p>
            <p class="" style="margin: 0in; font-size: 10pt; font-family: Calibri, sans-serif; text-align: center;" align="center"><span style="font-size: 11pt; display: none;" class="">&nbsp;</span></p>
            <div align="center" class="" style="">
               <table class="" style="width: 100%;" border="0" cellspacing="0" cellpadding="0">
                  <tbody class="" style="">
                     <tr class="" style="">
                        <td style="border-top: none; border-right: none; border-left: none; border-image: initial; border-bottom: 2.25pt solid rgb(1, 19, 82); padding: 0in;" xml="lang" class=""></td>
                     </tr>
                  </tbody>
               </table>
            </div>
            <div class="" style="">
             
               <table class="" style="width: 100%;" border="0" cellspacing="0" cellpadding="0">
                  <tbody class="" style="">
                     <tr class="" style="">
                        <td style="padding: 0pt 0pt 15pt;" xml="lang" class="">
                          
                           <p class="" style="margin: 0in; font-size: 10pt; font-family: Calibri, sans-serif; text-align: center;" align="center"><span style="font-size: 11pt;" class="">&nbsp;</span></p>
                           <div align="center" class="" style="">
                              <table class="" style="width: 100%;" border="0" cellspacing="0" cellpadding="0">
                                 <tbody class="" style="">
                                    <tr class="" style="">
                                       <td style="padding: 0in;" xml="lang" class="">
                                          <table class="" style="width: 100%;" border="0" cellspacing="0" cellpadding="0">
                                             <tbody class="" style="">
                                                <tr class="" style="">
                                                   <td style="padding: 0in;" xml="lang" class="">
                                                      <p class="" style="margin: 0; margin-left: 0in; font-size: 11pt; font-family: Calibri, sans-serif;  line-height: 18.75pt; text-align: left;"><span style="font-size: 15pt; font-family: Arial, sans-serif; color: black; " class="">Reservation ID: </span></p>
                                                   </td>
                                                   <td style="padding: 0in;" xml="lang" class="">
                                                      <p class="" style="margin: 0; margin-left: 0in; font-size: 11pt; font-family: Calibri, sans-serif; text-align: right; line-height: 18.75pt;" align="right"><span style="font-size: 15pt; font-family: Arial, sans-serif; color: black;" class="">${reservationId} </span></p>
                                                   </td>
                                                </tr>
                                             </tbody>
                                          </table>
                                       </td>
                                    </tr>
                                 </tbody>
                              </table>
                           </div>
                           <p class="" style="margin: 0in; font-size: 10pt; font-family: Calibri, sans-serif; text-align: center;" align="center"><span style="font-size: 11pt;" class="">&nbsp;</span></p>
                           <div align="center" class="" style="">
                              <table class="" style="width: 100%;" border="0" cellspacing="0" cellpadding="0">
                                 <tbody class="" style="">
                                    <tr class="" style="">
                                       <td style="padding: 0in;" xml="lang" class="">
                                          <table class="" style="width: 100%;" border="0" cellspacing="0" cellpadding="0">
                                             <tbody class="" style="">
                                                <tr class="" style="">
                                                   <td style="padding: 0in;" xml="lang" class="">
                                                      <p class="" style="margin: 0;; margin-left: 0in; font-size: 11pt; font-family: Calibri, sans-serif; text-align: left; line-height: 18.75pt;"><span style="font-size: 15pt; font-family: Arial, sans-serif; color: black;" class="">Station ID: </span></p>
                                                   </td>
                                                   <td style="padding: 0in;" xml="lang" class="">
                                                      <p class="" style="margin: 0in; margin-left: 0in; font-size: 11pt; font-family: Calibri, sans-serif;  text-align: right; line-height: 18.75pt;" align="right"><span style="font-size: 15pt; font-family: Arial, sans-serif; color: black;" class="">${stationId} </span></p>
                                                   </td>
                                                </tr>
                                             </tbody>
                                          </table>
                                       </td>
                                    </tr>
                                 </tbody>
                              </table>
                           </div>
						   <p class="" style="margin: 0in; font-size: 10pt; font-family: Calibri, sans-serif; text-align: center;" align="center"><span style="font-size: 11pt;" class="">&nbsp;</span></p>
                           <div align="center" class="" style="">
                              <table class="" style="width: 100%;" border="0" cellspacing="0" cellpadding="0">
                                 <tbody class="" style="">
                                    <tr class="" style="">
                                       <td style="padding: 0in;" xml="lang" class="">
                                          <table class="" style="width: 100%;" border="0" cellspacing="0" cellpadding="0">
                                             <tbody class="" style="">
                                                <tr class="" style="">
                                                   <td style="padding: 0in;" xml="lang" class="">
                                                      <p class="" style="margin: 0;; margin-left: 0in; font-size: 11pt; font-family: Calibri, sans-serif; text-align: left; line-height: 18.75pt;"><span style="font-size: 15pt; font-family: Arial, sans-serif; color: black;" class="">Connector ID: </span></p>
                                                   </td>
                                                   <td style="padding: 0in;" xml="lang" class="">
                                                      <p class="" style="margin: 0in; margin-left: 0in; font-size: 11pt; font-family: Calibri, sans-serif;  text-align: right; line-height: 18.75pt;" align="right"><span style="font-size: 15pt; font-family: Arial, sans-serif; color: black;" class="">${connectorId} </span></p>
                                                   </td>
                                                </tr>
                                             </tbody>
                                          </table>
                                       </td>
                                    </tr>
                                 </tbody>
                              </table>
                           </div>
                           <p class="" style="margin: 0in; font-size: 10pt; font-family: Calibri, sans-serif; text-align: center;" align="center"><span style="font-size: 11pt;" class="">&nbsp;</span></p>
                           <div align="center" class="" style="">
                              <table class="" style="width: 100%;" border="0" cellspacing="0" cellpadding="0">
                                 <tbody class="" style="">
                                    <tr class="" style="">
                                       <td style="padding: 0in;" xml="lang" class="">
                                          <table class="" style="width: 100%;" border="0" cellspacing="0" cellpadding="0">
                                             <tbody class="" style="">
                                                <tr class="" style="">
                                                   <td style="padding: 0in;" xml="lang" class="">
                                                      <p class="" style="margin: 0in; margin-left: 0in; font-size: 11pt; font-family: Calibri, sans-serif;  text-align: left; line-height: 18.75pt;"><span style="font-size: 15pt; font-family: Arial, sans-serif; color: black;" class="">Refund Amount: </span></p>
                                                   </td>
                                                   <td style="padding: 0in;" xml="lang" class="">
                                                      <p class="" style="margin: 0in; margin-left: 0in; font-size: 11pt; font-family: Calibri, sans-serif;  text-align: right; line-height: 18.75pt;" align="right"><span style="font-size: 15pt; font-family: Arial, sans-serif; color: black;" class="">${refund} </span></p>
                                                   </td>
                                                </tr>
                                             </tbody>
                                          </table>
                                       </td>
                                    </tr>
                                 </tbody>
                              </table>
                           </div>
                           
                           <p class="" style="margin: 0in; font-size: 10pt; font-family: Calibri, sans-serif; text-align: center;" align="center"><span style="font-size: 11pt;" class="">&nbsp;</span></p>
                        </td>
                     </tr>
                  </tbody>
               </table>
            </div>
            <div align="center" class="" style="">
               <table class="" style="width: 100%;" border="0" cellspacing="0" cellpadding="0">
                  <tbody class="" style="">
                     <tr class="" style="">
                        <td style="border-top: none; border-right: none; border-left: none; border-image: initial; border-bottom: 2.25pt solid rgb(1, 19, 82); padding: 0in;" xml="lang" class=""></td>
                     </tr>
                  </tbody>
               </table>
            </div>  
            <p class="" style="margin-right: 18.75pt; margin-left: 18.75pt; font-size: 11pt; font-family: Calibri, sans-serif; margin-bottom: 30pt; text-align: center; line-height: 12.75pt;" align="center"><span style="font-size: 10pt; font-family: Arial, sans-serif; color: black;" class=""><br class="" style=""> © 2021 <a href="${portalUrl}" target="_blank" class="" style=""> <strong class="" style=""><span style="text-decoration: none; color: rgb(1, 19, 82);" class="">${whiteLabelName}.</span></strong></a> All Rights Reserved. <br class="" style="">${orgAddress}</span></p>
         </td>
      </table>
   </body>
</html>