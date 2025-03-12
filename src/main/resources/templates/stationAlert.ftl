<!DOCTYPE html>
<html>
   <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1">
      <meta http-equiv="X-UA-Compatible" content="IE=edge">
      <title>Station-down alert</title>
      <style>
         .heading {
         padding: 7px 10px;
         width: 220px;
         font-weight: 600; font-size: 13px;
         }
      </style>
   </head>
   <body bgcolor="#ebebeb" style="margin: 0;background-color: #ebebeb;padding: 0;font-family: Arial, Helvetica, sans-serif;font-size: 13px;line-height: 1.5;color: #3e3935;">
      <table width="100%" cellspacing="0" cellpadding="0" border="0" style="border-collapse: collapse;">
      <tr>
         <td align="center" bgcolor="#ebebeb" style="border-width: 0;font-family: Arial, Helvetica, sans-serif;">
            <table class="container" cellspacing="0" cellpadding="0" border="0" style="border-collapse: collapse;max-width: 500px;width: 100%;">
               <tr>
                  <td class="superheader" bgcolor="#ffffff" align="left" style="border-width: 0;font-family: Arial, Helvetica, sans-serif;background-color: #ffffff;padding: 25px 20px;text-align: left;">
                     <table cellspacing="0" cellpadding="0" border="0" style="border-collapse: collapse;max-width: 440px;width: 100%;">
                        <tr>
                           <td class="logo" rowspan="2" valign="middle" align="left" style="border-width: 0;font-family: Arial, Helvetica, sans-serif;text-align: left;padding: 0 20px 0 0;"><a href="https://www.bchydro.com/?utm_source=transactional&utm_medium=email&utm_content=header" style="text-decoration: underline;color: #0074a4;"><img src="https://www.bchydro.com/content/dam/BCHydro/email/images/global/bch-logo.jpg" width="150" height="45" border="0" alt="BC Hydro: Power Smart" style="display: block;border: none;"></a></td>
                        </tr>
                     </table>
                  </td>
               </tr>
               <tr>
                  <td class="header" bgcolor="#004F6C" align="left" style="border-width: 0;font-family: Arial, Helvetica, sans-serif;background-color: #004F6C;text-align: left;">
                     <table cellspacing="0" cellpadding="0" border="0" style="border-collapse: collapse;width: 100%;">
                        <tr>
                           <td class="title" align="left" style="border-width: 0;font-family: Arial, Helvetica, sans-serif;text-align: left;font-size: 42px;font-weight: bold;color: #ffffff;">
                              <p align="left" style="background-color: #004F6C;margin: 0 15px 0; font-size: 42px;line-height: 1.5;font-weight: 700;bold;color: #ffffff;">Station alert </p>
                           </td>
                        </tr>
                     </table>
                  </td>
               </tr>
               <tr>
                  <td class="content" bgcolor="#ffffff" align="left" style="border-width: 0;font-family: Arial, Helvetica, sans-serif;background-color: #ffffff;padding: 40px 20px 20px 20px;text-align: left;">
                     <p style="margin: 0 0 15px 0;">${curDate}<br/>${event}<br/>${description}</p>
                     <table>
                        <table cellspacing="0" cellpadding="0" border="0" style="width:100%;background:#05A1C6;">
                           <tbody>
                              <tr>
                                 <td style="font-family: Arial, Helvetica, sans-serif;background: #05A1C6;padding:10px;box-sizing:border-box;">
                                    <h2 style="margin:0; color: white;font-size: 16px;font-weight: 700;">Station Details</h2>
                                 </td>
                              </tr>
                           </tbody>
                        </table>
                        <table cellspacing="0" cellpadding="0" border="0" bgcolor="#f5f3f2" style="background:#f5f3f2;width:100%;">
                           <tbody>
                              <tr>
                                 <td bgcolor="#f5f3f2" style="font-family: Arial, Helvetica, sans-serif;width: 160px;box-sizing: border-box;padding: 5px 10px;gap: 4px;border-right: 1px solid #FFF; border-bottom: 1px solid #FFF; background: #f5f3f2;">Charger ID:</td>
                                 <td bgcolor="#f5f3f2" style="font-family: Arial, Helvetica, sans-serif;font-weight: 700; padding: 5px 10px;gap: 4px;border-right: 1px solid #FFF; border-bottom: 1px solid #FFF; background: #f5f3f2;">${StationId}</td>
                              </tr>
							  <tr>
                                 <td bgcolor="#f5f3f2" style="font-family: Arial, Helvetica, sans-serif;width: 160px;box-sizing: border-box;padding: 5px 10px;gap: 4px;border-right: 1px solid #FFF; border-bottom: 1px solid #FFF; background: #f5f3f2;">Status:</td>
                                 <td bgcolor="#f5f3f2" style="font-family: Arial, Helvetica, sans-serif;font-weight: 700; padding: 5px 10px;gap: 4px;border-right: 1px solid #FFF; border-bottom: 1px solid #FFF; background: #f5f3f2;">${Status}</td>
                              </tr>
                           </tbody>
                        </table>
						<p style="margin: 0 0 15px 0;">&nbsp;</p>
						<p style="margin: 0 0 15px 0;">Questions? Reach us 24 hours a day at ${support_phone} or by email at <a href="evsupport@bchydro.com" style="text-decoration:underline;color: #0074a4;font-weight: bold;">${support_mail}</a></p>
                        </td>
                        </tr>
                        <tr>
                           <td class="links" bgcolor="#F5F5F5" align="left" style="border-width: 0;font-family: Arial, Helvetica, sans-serif;background-color: #F5F5F5;padding: 20px 20px 10px 20px;text-align: left;">
                              <table cellspacing="0" cellpadding="0" border="0" style="border-collapse: collapse;max-width: 440px;width: 100%;">
                                 <tr>
                                    
                                    <td align="left" style="border-width: 0;font-family: Arial, Helvetica, sans-serif;text-align: left;">
                                       <table align="left" cellspacing="0" cellpadding="0" border="0" style="border-collapse: collapse;min-width: 0;width: auto;">
                                          <tr>
                                             <td align="left" style="border-width: 0;font-family: Arial, Helvetica, sans-serif;text-align: left;padding: 0 30px 10px 0;">
                                                <a href="https://electricvehicles.bchydro.com/charge/public-charging?utm_source=ev&utm_medium=email&utm_content=footer" style="text-decoration: underline;color: #000000;font-weight: bold;">BC Hydro EV</a>
                                             </td>
                                          </tr>
                                       </table>
                                       <table align="left" cellspacing="0" cellpadding="0" border="0" style="border-collapse: collapse;min-width: 0;width: auto;">
                                          <tr>
                                             <td align="left" style="border-width: 0;font-family: Arial, Helvetica, sans-serif;text-align: left;padding: 0 30px 10px 0;">
                                                <a href="https://electricvehicles.bchydro.com/charge/public-charging/how-use-our-fast-chargers?utm_source=ev&utm_medium=email&utm_content=footer" style="text-decoration: underline;color: #000000;font-weight: bold;">Charging support</a>
                                             </td>
                                          </tr>
                                       </table>
                                       <table align="left" cellspacing="0" cellpadding="0" border="0" style="border-collapse: collapse;min-width: 0;width: auto;">
                                          <tr>
                                             <td align="left" style="border-width: 0;font-family: Arial, Helvetica, sans-serif;text-align: left;padding: 0 30px 10px 0;">
                                                <a href="https://electricvehicles.bchydro.com/contact-us?utm_source=ev&utm_medium=email&utm_content=footer" style="text-decoration: underline;color: #000000;font-weight: bold;">Contact us</a>
                                             </td>
                                          </tr>
                                       </table>
                                    </td>
                                 </tr>
                              </table>
                           </td>
                        </tr>
                        <tr>
                           <td class="footer" bgcolor="#ffffff" align="left" style="border-width: 0;font-family: Arial, Helvetica, sans-serif;background-color: #F5F5F5;padding: 0 20px;text-align: left;">
                              <table cellspacing="0" cellpadding="0" border="0" style="border-collapse: collapse;max-width: 440px;width: 100%;">
                                 <tr>
                                    <td align="left" style="border-width: 0;font-family: Arial, Helvetica, sans-serif;text-align: left;border-top: 1px solid #000000;font-size: 11px;color: #000000;padding: 20px 0 20px 0;">
                                       <p style="margin: 0 0 15px 0;">You've received this email at <a href="mailto:${to_mail}" style="text-decoration: underline;color: #000000;font-weight: bold;display: inline-block;">${to_mail}</a> because
                                          you've registered with the BC Hydro EV network.
                                       </p>
                                       <p style="margin: 0 0 15px 0;">© BC Hydro, <span class="address">333 Dunsmuir&zwj; Street, Vancouver, B.C.
                                          V6B 5R3</span> | <a href="https://www.bchydro.com/siteinfo/privacy.html?utm_source=transactional&utm_medium=email&utm_content=privacy" style="text-decoration: underline;color: #000000;font-weight: bold;display: inline-block;">Privacy
                                          statement</a>
                                       </p>
                                       <table class="social" style="border-collapse: collapse;min-width: unset;width: 130px;">
                                          <tbody>
                                             <tr>
                                                <td style="border-width: 0;font-family: Arial, Helvetica, sans-serif;text-align: left;border-top: none;font-size: 11px;color: #000000;padding: 0;">
                                                   <a href="https://www.facebook.com/bchydro" style="text-decoration: underline;color: #000000;font-weight: bold;display: inline-block;"><img src="https://www.bchydro.com/content/dam/BCHydro/email/images/content/facebook-blk.png" alt="Facebook" width="20" height="20" style="display: block;border: none;margin-right: 10px;"></a>
                                                </td>
                                                <td style="border-width: 0;font-family: Arial, Helvetica, sans-serif;text-align: left;border-top: none;font-size: 11px;color: #000000;padding: 0;">
                                                   <a href="https://www.instagram.com/bchydro/" style="text-decoration: underline;color: #000000;font-weight: bold;display: inline-block;"><img src="https://www.bchydro.com/content/dam/BCHydro/email/images/content/instagram-blk.png" alt="Instagram" width="20" height="20" style="display: block;border: none;margin-right: 10px;"></a>
                                                </td>
                                                <td style="border-width: 0;font-family: Arial, Helvetica, sans-serif;text-align: left;border-top: none;font-size: 11px;color: #000000;padding: 0;">
                                                   <a href="https://www.tiktok.com/@bchydro?lang=en" style="text-decoration: underline;color: #000000;font-weight: bold;display: inline-block;"><img src="https://www.bchydro.com/content/dam/BCHydro/email/images/content/tiktok-blk.png" alt="YouTube" width="20" height="20" style="display: block;border: none;margin-right: 10px;"></a>
                                                </td>
                                                <td style="border-width: 0;font-family: Arial, Helvetica, sans-serif;text-align: left;border-top: none;font-size: 11px;color: #000000;padding: 0;">
                                                   <a href="https://twitter.com/bchydro" style="text-decoration: underline;color: #000000;font-weight: bold;display: inline-block;"><img src="https://www.bchydro.com/content/dam/BCHydro/email/images/content/twitter-blk.png" alt="Twitter" width="20" height="20" style="display: block;border: none;margin-right: 10px;"></a>
                                                </td>
                                                <td style="border-width: 0;font-family: Arial, Helvetica, sans-serif;text-align: left;border-top: none;font-size: 11px;color: #000000;padding: 0;">
                                                   <a href="https://www.youtube.com/user/BChydro" style="text-decoration: underline;color: #000000;font-weight: bold;display: inline-block;"><img src="https://www.bchydro.com/content/dam/BCHydro/email/images/content/youtube-blk.png" alt="YouTube" width="20" height="20" style="display: block;border: none;margin-right: 10px;"></a>
                                                </td>
                                             </tr>
                                          </tbody>
                                       </table>
                                    </td>
                                 </tr>
                              </table>
                           </td>
                        </tr>
                     </table>
                  </td>
               </tr>
            </table>
   </body>
</html>