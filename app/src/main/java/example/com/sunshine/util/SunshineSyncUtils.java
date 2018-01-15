package example.com.sunshine.util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import example.com.sunshine.sync.SunshineSyncIntentService;

public class SunshineSyncUtils {

    /**
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    public static void startImmediateSynt(@NonNull final Context context) {
        Intent intent = new Intent(context, SunshineSyncIntentService.class);
        context.startService(intent);
    }
}
