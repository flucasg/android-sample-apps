package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.configuration.VisualOnConfiguration;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.android.visualon.PersonalizationAsyncTask;
import com.ooyala.android.visualon.PersonalizationCallback;
import com.ooyala.sample.R;

import java.util.Observable;
import java.util.Observer;

/**
 * This activity illustrates how you enable SecurePlayer video playback.
 *
 */
public class SecurePlayerPrePersonalizedPlayerActivity extends Activity implements Observer {
  final String TAG = this.getClass().toString();

  String EMBED = null;
  final String PCODE  = "FoeG863GnBL4IhhlFC1Q2jqbkH9m";
  final String DOMAIN = "http://ooyala.com";

  protected OoyalaPlayerLayoutController playerLayoutController;
  protected OoyalaPlayer player;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getIntent().getExtras().getString("selection_name"));
    setContentView(R.layout.player_simple_layout);
    EMBED = getIntent().getExtras().getString("embed_code");

    OoyalaPlayer.enableCustomHLSPlayer = true;
    OoyalaPlayer.enableCustomPlayreadyPlayer = true;

    // Mandatory - You need to get an OPID for your application (setSessionId)
    // and need to reference the version string for your specific SecurePlayer Libraries.
    // Talk to your CSM or Technical Support for more information
    VisualOnConfiguration voOpts = new VisualOnConfiguration.Builder().setSessionId("session").setVersion("GENERAL_ANDR_VOP_PROB_RC_03_08_02_0000").build();
    Options options = new Options.Builder().setVisualOnConfiguration(voOpts).build();

    //Initialize the player
    OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
    player = new OoyalaPlayer(PCODE, new PlayerDomain(DOMAIN), options);

    playerLayoutController = new OoyalaPlayerLayoutController(playerLayout, player);
    player.addObserver(this);
    
    new PersonalizationAsyncTask(
        new PersonalizationCallback() {
          @Override
          public void afterPersonalization(Exception exception) {
            Log.d(TAG, "afterPersonalization: " + (exception == null ? "OK" : exception.toString()));
            if (player.setEmbedCode(EMBED)) { player.play(); }
            else { Log.e(TAG, "Asset Failure"); }
          }
        },
        this,
        new VisualOnConfiguration.Builder().build().getPersonalizationServerUrl(),
        false,
        "session", // Mandatory - You need to get an OPID for your application. Talk to your CSM or Technical Support for more information
        "GENERAL_ANDR_VOP_PROB_RC_03_08_02_0000"
    ).execute();
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.d(TAG, "Player Activity Stopped");
    if (player != null) {
      player.suspend();
    }
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    Log.d(TAG, "Player Activity Restarted");
    if (player != null) {
      player.resume();
    }
  }

  /**
   * Listen to all notifications from the OoyalaPlayer
   */
  @Override
  public void update(Observable arg0, Object argN) {
    if (arg0 != player) {
      return;
    }

    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      return;
    }

    if (arg1 == OoyalaPlayer.ERROR_NOTIFICATION_NAME) {
      final String msg = "Error Event Received";
      if (player != null && player.getError() != null) {
        Log.e(TAG, msg, player.getError());
      }
      else {
        Log.e(TAG, msg);
      }
      return;
    }

    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }

}
