package com.example.jetnews.ui.page01

import android.util.Log
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jetnews.ui.home.HomeUiState
import com.example.jetnews.ui.home.HomeViewModel

@Composable
fun PageOne(
    homeViewModel: HomeViewModel,
    isExpandedScreen: Boolean,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
){
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    PageOne(
        uiState = uiState,
        isExpandedScreen = isExpandedScreen,
        onToggleFavorite = { homeViewModel.toggleFavourite(it) },
        onSelectPost = { homeViewModel.selectArticle(it) },
        onRefreshPosts = { homeViewModel.refreshPosts() },
        onErrorDismiss = { homeViewModel.errorShown(it) },
        onInteractWithFeed = { homeViewModel.interactedWithFeed() },
        onInteractWithArticleDetails = { homeViewModel.interactedWithArticleDetails(it) },
        onSearchInputChanged = { homeViewModel.onSearchInputChanged(it) },
        openDrawer = openDrawer,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun PageOne(
    uiState: HomeUiState,
    isExpandedScreen: Boolean,
    onToggleFavorite: (String) -> Unit,
    onSelectPost: (String) -> Unit,
    onRefreshPosts: () -> Unit,
    onErrorDismiss: (Long) -> Unit,
    onInteractWithFeed: () -> Unit,
    onInteractWithArticleDetails: (String) -> Unit,
    onSearchInputChanged: (String) -> Unit,
    openDrawer: () -> Unit,
    snackbarHostState: SnackbarHostState
){
    val homeListLazyListState = rememberLazyListState()
    val articleDetailLazyListStates = when (uiState) {
        is HomeUiState.HasPosts -> uiState.postsFeed.allPosts
        is HomeUiState.NoPosts -> emptyList()
    }.associate { post ->
        key(post.id) {
            post.id to rememberLazyListState()
        }
    }
    when (getHomeScreenType(isExpandedScreen, uiState)) {
        PageType.FeedWithArticleDetails ->{
            Log.d("PageOne","PageType.FeedWithArticleDetails")
            Text("摘要和详情")
        }
        PageType.Feed ->{
            Log.d("PageOne","PageType.Feed")
            ArticleFeed(
                uiState = uiState,
                showTopAppBar = !isExpandedScreen,
                onToggleFavorite = onToggleFavorite,
                onSelectPost = onSelectPost,
                onRefreshPosts = onRefreshPosts,
                onErrorDismiss = onErrorDismiss,
                openDrawer = openDrawer,
                homeListLazyListState = homeListLazyListState,
                snackbarHostState = snackbarHostState,
                onSearchInputChanged = onSearchInputChanged,
            )

        }
        PageType.ArticleDetails ->{
            Log.d("PageOne","PageType.ArticleDetails")
            Text("详情")
        }
    }


}




private enum class PageType {
    FeedWithArticleDetails,
    Feed,
    ArticleDetails
}

@Composable
private fun getHomeScreenType(
    isExpandedScreen: Boolean,
    uiState: HomeUiState
): PageType = when (isExpandedScreen) {
    false -> {
        when (uiState) {
            is HomeUiState.HasPosts -> {
                if (uiState.isArticleOpen) {
                    PageType.ArticleDetails
                } else {
                    PageType.Feed
                }
            }
            is HomeUiState.NoPosts -> PageType.Feed
        }
    }
    true -> PageType.FeedWithArticleDetails
}
