package seker.multimedia;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.AdapterView.OnItemLongClickListener;
import seker.common.BaseFragment;
import seker.common.cache.DiskImageCache;
import seker.common.net.HttpRequestInfo;
import seker.common.net.HttpRequester;
import seker.common.net.IResponseParser;
import seker.common.net.ParamPair;
import seker.common.net.ResponseHandler;
import seker.common.net.IResponseHandler.IResponseCallback;
import seker.common.net.transport.ITransportCallback;
import seker.common.net.transport.TransportTaskInfo;
import seker.common.net.transport.download.Downloader;
import seker.common.net.transport.upload.Uploader;
import seker.common.widget.WebImageView;

public class VideoFragment  extends BaseFragment implements OnClickListener, OnItemLongClickListener {
    
    private GridView mGridView;
    
    private VideoAdapter mVideoAdapter;
    
    private List<String> mUrls = new ArrayList<String>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        
        View view = inflater.inflate(R.layout.root, container, false);
        
        view.findViewById(R.id.upload).setOnClickListener(this);
        
        mGridView = (GridView) view.findViewById(R.id.gridview);
        
        mGridView.setOnItemLongClickListener(this);
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        refreshFileList();
    }
    
    private void refreshFileList() {
        HttpRequestInfo info = new HttpRequestInfo("", HttpRequestInfo.HTTP_GET);
        List<ParamPair<?>> list = null;
        IResponseParser<InputStream, List<String>> parser = null;
        IResponseCallback<List<String>> callback = new IResponseCallback<List<String>>() {
            @Override
            public void handleResponse(List<ParamPair<String>> headers, List<String> urls) {
                mUrls.clear();
                mUrls.addAll(urls);
                if (null == mVideoAdapter) {
                    mVideoAdapter = new VideoAdapter(mActivity, mUrls);
                    mGridView.setAdapter(mVideoAdapter);
                } else {
                    mVideoAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void handleNoResponse(List<ParamPair<String>> headers) {
            }
            @Override
            public void handleNetException() {
            }
        };
        ResponseHandler<List<String>> handler = new ResponseHandler<List<String>>(info, callback);
        HttpRequester<List<String>> requester = new HttpRequester<List<String>>(mActivity);
        requester.requestAsync(info, list, parser, handler);
    }
    @Override
    public void onClick(View v) {
        Intent intent = SystemSupport.getStartVideoIntent();
        startActivityForResult(intent, 0);
    }
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (Activity.RESULT_OK == resultCode) {
            Uri uri = data.getData();
            String file = SystemSupport.getRealPathFromURI(mActivity, uri);
            if (LOG) {
                Log.d(TAG, file);
            }
            
            TransportTaskInfo info = new TransportTaskInfo("url", file);
            info.setKeys(new String[] {"key"});
            
            ITransportCallback tclassback = new ITransportCallback() {
                @Override
                public void onStart() {
                }
                @Override
                public void onProgress(long curr, long total) {
                }
                @Override
                public void onFinished() {
                }
                @Override
                public void onError() {
                }
                @Override
                public void onCanceled() {
                }
            };
            new Uploader(mActivity, info).start(tclassback);
        }
    }
    
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        String url = mUrls.get(position);
        TransportTaskInfo info = new TransportTaskInfo(url, DiskImageCache.getLocalPathFromUrl(url));
        
        ITransportCallback tcallback = new ITransportCallback() {
            @Override
            public void onStart() {
            }
            @Override
            public void onProgress(long curr, long total) {
            }
            @Override
            public void onFinished() {
            }
            @Override
            public void onError() {
            }
            @Override
            public void onCanceled() {
            }
        };
        
        new Downloader(mActivity, info).start(tcallback);
        return true;
    }
    
    static class VideoAdapter extends BaseAdapter {

        private List<String> mUrls;
        private LayoutInflater mInflater;
        
        public VideoAdapter(Context context, List<String> urls) {
            mInflater = LayoutInflater.from(context);
            mUrls = urls;
        }
        
        @Override
        public int getCount() {
            return mUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return mUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View item = null;
            ViewHolder holder = null;
            if (null == convertView) {
                item = mInflater.inflate(R.layout.item, null);
                
                holder = new ViewHolder();
                holder.image = (WebImageView) item;
                
                item.setTag(holder);
            } else {
                item = convertView;
                
                holder = (ViewHolder) item.getTag();
            }
            
            holder.image.setImageBitmap(null);
            holder.image.loadWebImage(mUrls.get(position));
            
            return item;
        }
        
        class ViewHolder {
            WebImageView image;
        }
    }
}