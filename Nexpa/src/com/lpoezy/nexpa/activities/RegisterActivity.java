package com.lpoezy.nexpa.activities;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;

import com.lpoezy.nexpa.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	private Connection connection;
	
	EditText username, password, email, fullName;
	Button register;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		email = (EditText) findViewById(R.id.email);
		fullName = (EditText) findViewById(R.id.fullName);
		register = (Button) findViewById(R.id.register);
		
		  register.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					
					AccountManager am = new AccountManager(connection);
					Map<String, String> attributes = new HashMap<String, String>();
					attributes.put("username", "my_user_name");
					attributes.put("password", "my_password");
					attributes.put("email", "foo@foo.com");
					attributes.put("name", "my_full_name");
//					am.createAccount("my_user_name", "my_password", attributes);


					Registration reg = new Registration();
					reg.setType(IQ.Type.SET);
					reg.setTo(connection.getServiceName());
//					      attributes.put("username", username);
//					      attributes.put("password", password);
//					      reg.setAttributes(attributes);
//					reg.addAttribute("username", username);
//					reg.addAttribute("password", password);
//					reg.addAttribute("email", email);
//					reg.addAttribute("name", fullName);
					PacketFilter filter = new AndFilter(new PacketIDFilter(
					    reg.getPacketID()), new PacketTypeFilter(IQ.class));
					PacketCollector collector = connection.createPacketCollector(filter);
					connection.sendPacket(reg);
		    	
					Toast.makeText(getApplicationContext(),
		                      "Button Clicked",Toast.LENGTH_SHORT).show();	
					
				}

		    });
			
	}

	 

}
