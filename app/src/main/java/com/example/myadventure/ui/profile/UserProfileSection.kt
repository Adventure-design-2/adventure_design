import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.myadventure.R

@Composable
fun UserProfileSection(userName: String, profileImageUri: Uri?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = profileImageUri?.let { rememberAsyncImagePainter(it) }
                ?: painterResource(id = R.drawable.ic_profile),
            contentDescription = "프로필 사진",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .padding(end = 16.dp)
        )
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}