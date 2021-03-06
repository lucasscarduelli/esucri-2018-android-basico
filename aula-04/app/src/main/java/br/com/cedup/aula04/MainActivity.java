package br.com.cedup.aula04;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.cedup.aula04.controller.ProdutoController;
import br.com.cedup.aula04.model.Produto;

public class MainActivity extends AppCompatActivity {

    EditText codigo, descricao, marca, precoBase;
    String idProduto;
    Produto produto;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_opcao1:
                Intent intent = new Intent(this, ConsultaProdutoActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_opcao2:
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        codigo = findViewById(R.id.codigo);
        descricao = findViewById(R.id.descricao);
        marca = findViewById(R.id.marca);
        precoBase = findViewById(R.id.precoBase);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tela Principal");
        actionBar.setDisplayHomeAsUpEnabled(true);

        ArrayList<String> permissoesRequisitar = new ArrayList<>();
        String[] permissoesRequisitarArray = new String[]{};

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            permissoesRequisitar.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            permissoesRequisitar.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissoesRequisitar.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissoesRequisitar.toArray(permissoesRequisitarArray), 1);
        }

        idProduto = this.getIntent().getStringExtra("id");
        if (!TextUtils.isEmpty(idProduto)) {
            ProdutoController crud = new ProdutoController(getBaseContext());
            produto = crud.getById(Integer.parseInt(idProduto));

            codigo.setText(produto.getId().toString());
            descricao.setText(produto.getDescricao());
            marca.setText(produto.getMarca());
            precoBase.setText(produto.getPrecoBase().toString());
        }

    }

    public void salvar(View view) {

        if (!validaCampos()) {
            return;
        }

        Produto produto = new Produto();
        produto.setDescricao(descricao.getText().toString());
        produto.setMarca(marca.getText().toString());
        produto.setPrecoBase(
                BigDecimal.valueOf(
                        Double.parseDouble(precoBase.getText().toString())
                ));

        ProdutoController crud = new ProdutoController(getBaseContext());
        long retorno;

        if (TextUtils.isEmpty(idProduto)) {
            retorno = crud.create(produto);
        } else {
            produto.setId(Integer.parseInt(idProduto));
            retorno = crud.update(produto);
        }

        if (retorno == -1) {
            Toast.makeText(this, "Erro ao salvar o registro!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Registro salvo com sucesso!", Toast.LENGTH_LONG).show();
            limpar();
        }
    }

    private boolean validaCampos() {
        Boolean result = true;

        String textoDescricao = descricao.getText().toString().trim();
        if (TextUtils.isEmpty(textoDescricao)) {
            descricao.requestFocus();
            descricao.setError("Campo obrigatório!");
            return false;
        }

        return result;
    }

    public void excluir(View view) {

        if (TextUtils.isEmpty(idProduto)) {
            Toast.makeText(getBaseContext(),
                    "Este produto ainda não está no banco de dados!", Toast.LENGTH_LONG).show();
            return;
        }

        ProdutoController crud = new ProdutoController(getBaseContext());
        long resultado = crud.delete(produto);

        limpar();

        if (resultado == -1) {
            Toast.makeText(getBaseContext(),"Erro ao excluir produto!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(),"Produto excluído com sucesso!", Toast.LENGTH_LONG).show();
        }

    }

    private void limpar() {
        idProduto = null;
        produto = null;

        codigo.setText("");
        descricao.setText("");
        marca.setText("");
        precoBase.setText("");
    }
}
