package seker.multimedia;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import seker.common.BaseFragment;
import seker.common.cache.DiskImageCache;
import seker.common.net.HttpRequestInfo;
import seker.common.net.HttpRequester;
import seker.common.net.IResponseHandler.IResponseCallback;
import seker.common.net.IResponseParser;
import seker.common.net.ParamPair;
import seker.common.net.ResponseHandler;
import seker.common.net.transport.ITransportCallback;
import seker.common.net.transport.TransportTaskInfo;
import seker.common.net.transport.download.Downloader;
import seker.common.net.transport.upload.Uploader;
import seker.common.widget.WebImageView;
import seker.multimedia.bean.FileStatus;
import seker.multimedia.bean.FileStatuses;
import seker.multimedia.parser.FileStatusesParser;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoFragment extends BaseFragment implements OnClickListener, OnItemLongClickListener {
    
    private GridView mGridView;
    
    private PhotoAdapter mPhotoAdapter;
    
    private List<FileStatus> mFiles = new ArrayList<FileStatus>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        
        View view = inflater.inflate(R.layout.root, container, false);
        
        view.findViewById(R.id.upload).setOnClickListener(this);
        
        mGridView = (GridView) view.findViewById(R.id.gridview);
        
        mGridView.setOnItemLongClickListener(this);
        
        refreshFileList();
        
        return view;
    }
    
    private void refreshFileList() {
        HttpRequestInfo info = new HttpRequestInfo(WebHdfs.SERVER + "?op=LISTSTATUS", HttpRequestInfo.HTTP_GET);
        List<ParamPair<?>> list = null;
        IResponseParser<InputStream, FileStatuses> parser = new FileStatusesParser();
        IResponseCallback<FileStatuses> callback = new IResponseCallback<FileStatuses>() {
            @Override
            public void handleResponse(List<ParamPair<String>> headers, FileStatuses files) {
                mFiles.clear();
                for (FileStatus file : files.FileStatuses.FileStatus) {
                    if (FileStatus.Type.FILE.name().equalsIgnoreCase(file.type)) {
                        mFiles.add(file);
                    }
                }
                if (null == mPhotoAdapter) {
                    mPhotoAdapter = new PhotoAdapter(mActivity, mFiles);
                    mGridView.setAdapter(mPhotoAdapter);
                } else {
                    mPhotoAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void handleNoResponse(List<ParamPair<String>> headers) {
            }
            @Override
            public void handleNetException() {
            }
        };
        ResponseHandler<FileStatuses> handler = new ResponseHandler<FileStatuses>(info, callback);
        HttpRequester<FileStatuses> requester = new HttpRequester<FileStatuses>(mActivity);
        requester.requestAsync(info, list, parser, handler);
    }
    @Override
    public void onClick(View v) {
        Intent intent = SystemSupport.getStartGalleryIntent();
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
            uploadStep1(file);
        }
    }
    
    private void uploadStep1(final String file) {
        String url = WebHdfs.SERVER + "?op=CREATE&overwrite=true&blocksize=" + new File(file).length() 
                + "&replication=0&permission=777&buffersize=1024";
        
        HttpRequestInfo info = new HttpRequestInfo(url, HttpRequestInfo.HTTP_PUT);
        List<ParamPair<?>> list = null;
        IResponseParser<InputStream, String> parser = null;
        IResponseCallback<String> callback = new IResponseCallback<String>() {
            @Override
            public void handleNetException() {
            }
            @Override
            public void handleNoResponse(List<ParamPair<String>> headers) {
                uploadStep2(file, headers);
            }
            @Override
            public void handleResponse(List<ParamPair<String>> headers, String r) {
                uploadStep2(file, headers);
            }
        };
        ResponseHandler<String> handler = new ResponseHandler<String>(info, callback);
        HttpRequester<String> requester = new HttpRequester<String>(mActivity);
        requester.requestAsync(info, list, parser, handler);
    }
    
    private void uploadStep2(final String file, List<ParamPair<String>> headers) {
        String url = null;
        for (ParamPair<String> header : headers) {
            if (TextUtils.equals("Location", header.getName()) ) {
                url = header.getValue();
                break;
            }
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        
        TransportTaskInfo info = new TransportTaskInfo(url, file);
        info.setKeys(new String[] { "LOCAL_FILE" });

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
    
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        FileStatus file = mFiles.get(position);
        String url = WebHdfs.SERVER + File.separator + file.pathSuffix + "?op=OPEN&offset=0&length=" + file.blockSize;
        TransportTaskInfo info = new TransportTaskInfo(url, DiskImageCache.getLocalPathFromUrl(WebHdfs.SERVER + file.pathSuffix));
        
        ITransportCallback tcallback = new ITransportCallback() {
            @Override
            public void onStart() {
            }
            @Override
            public void onProgress(long curr, long total) {
            }
            @Override
            public void onFinished() {
                if (LOG) {
                    Log.d(TAG, "download finish.");
                }
                
                Toast.makeText(mActivity, "download finish.", Toast.LENGTH_LONG).show();
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
    
    static class PhotoAdapter extends BaseAdapter {

        private List<FileStatus> mFiles;
        private LayoutInflater mInflater;
        
        public PhotoAdapter(Context context, List<FileStatus> files) {
            mInflater = LayoutInflater.from(context);
            mFiles = files;
        }
        
        @Override
        public int getCount() {
            return mFiles.size();
        }

        @Override
        public Object getItem(int position) {
            return mFiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View item = null;
            ViewHolder holder = null;
            FileStatus file = mFiles.get(position);
            if (null == convertView) {
                item = mInflater.inflate(R.layout.item, null);
                
                holder = new ViewHolder();
                holder.image = (WebImageView) item.findViewById(R.id.image);
                holder.name = (TextView) item.findViewById(R.id.name);
                
                item.setTag(holder);
            } else {
                item = convertView;
                
                holder = (ViewHolder) item.getTag();
            }
            
            holder.name.setText(file.pathSuffix);
            // holder.image.setImageBitmap(null);
            // holder.image.loadWebImage(WebHdfs.SERVER + mFiles.get(position).pathSuffix);
            
            return item;
        }
        
        class ViewHolder {
            WebImageView image;
            TextView name;
        }
    }
}
